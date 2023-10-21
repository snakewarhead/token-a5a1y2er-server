package com.cq.exchange.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.cq.core.vo.MailMsg;
import com.cq.exchange.NotifyContext;
import com.cq.exchange.entity.ExchangeCoinInfo;
import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.entity.ExchangeKline;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import com.cq.exchange.service.ServiceContext;
import com.cq.util.MathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class VolumeChangeQuickAnalyser implements Runnable {
    private final ServiceContext serviceContext;
    private final ExchangeEnum exchangeEnum;
    private final ExchangeTradeType tradeType;
    private final ExchangePeriodEnum periodEnum;
    private final BigDecimal multipleChange;

    private final static long TIME_STALE_NOTIFY = 24 * 3600 * 1000;
    private final static long PERIOD_STALE_TOLERANCE = 3L;

    private long periodStaleTolerance;
    private NotifyContext notifyContext;

    public VolumeChangeQuickAnalyser init() {
        periodStaleTolerance = periodEnum.getMillis() * PERIOD_STALE_TOLERANCE;
        notifyContext = new NotifyContext(TIME_STALE_NOTIFY);
        return this;
    }

    public String cron(String c) throws Exception {
        ExchangePeriodEnum cp = ExchangePeriodEnum.getEnum(c);
        if ("m".equals(cp.getUnit())) {
            return StrUtil.format("30 0/{} * * * ?", cp.getNum());
        }
        throw new Exception(StrUtil.format("cron not support - {}", cp.getSymbol()));
    }

    @Override
    public void run() {
        StopWatch sw = new StopWatch();
        sw.start(StrUtil.format("{}", this.getClass().getName()));

        try {
            List<ExchangeCoinInfoRaw> symbols = serviceContext.getExchangeCoinInfoRawService().find(exchangeEnum.getCode(), tradeType.getCode(), 1);
            for (var s : symbols) {
                try {
                    // get smooth volume which mustn't stale
                    ExchangeCoinInfo info = serviceContext.getExchangeCoinInfoService().find(exchangeEnum.getCode(), tradeType.getCode(), s.getSymbol(), periodEnum.getSymbol());
                    if (info == null) {
                        log.error("ExchangeCoinInfo is not found. {}", s.getSymbol());
                        continue;
                    }

                    long diffInfo = DateUtil.betweenMs(info.getDateUpdate(), DateUtil.date());
                    if (diffInfo > periodStaleTolerance) {
                        log.error("ExchangeCoinInfo is stale. {}, stale time: {}", s.getSymbol(), diffInfo);
                        continue;
                    }

                    // is volume over multipleChange?
                    ExchangeKline kline = serviceContext.getExchangeKlineService().findLatest(exchangeEnum.getCode(), tradeType.getCode(), s.getSymbol(), periodEnum.getSymbol());
                    if (kline == null) {
                        log.error("kline is not found. {}", s.getSymbol());
                        continue;
                    }

                    long diffKline = DateUtil.betweenMs(kline.getDateUpdate(), DateUtil.date());
                    if (diffKline > periodEnum.getMillis()) {
                        log.error("kline is stale. {}, stale time: {}", s.getSymbol(), diffKline);
                        continue;
                    }

                    // update db
                    BigDecimal volumeMultiple = MathUtil.div(kline.getVolume(), info.getQtyAvgSmoothVolume());
                    info.setMultipleVolume(volumeMultiple);
                    serviceContext.getExchangeCoinInfoService().updateOne(info);

                    if (volumeMultiple.compareTo(multipleChange) < 0) {
                        // no over
                        continue;
                    }

                    // notify
                    if (notifyContext.fresh(s.getSymbol())) {
                        String ct = htmlTables(DateUtil.date(kline.getOpenTime()).toString(), s, info, kline);
                        MailMsg msg = MailMsg.builder()
                                .subject("volume change quick")
                                .text(ct)
                                .silent(MailMsg.Silent.builder()
                                        .hash(SecureUtil.md5(info.getSymbol()))
                                        .deadline(TIME_STALE_NOTIFY)
                                        .build())
                                .build();
                        serviceContext.getMailClient().send(msg);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        sw.stop();
        log.info(sw.prettyPrint(TimeUnit.MILLISECONDS));
    }

    private String htmlTables(String tilte, ExchangeCoinInfoRaw infoRaw, ExchangeCoinInfo info, ExchangeKline kline) {
        // open time
        // symbol exchange
        List<String> headers = new ArrayList<>();
        headers.add("symbol");
        headers.add("exchange");

        headers.add("量比");
        headers.add("价格");
        headers.add("振幅");
        headers.add("平均振幅");

        headers.add("成交额差值");
        headers.add("买入额主动");
        headers.add("卖出额主动");
        headers.add("成交额");
        headers.add("成交额平均");

        headers.add("成交量");
        headers.add("成交量平均");
        headers.add("成交量标准差");
        headers.add("成交量标准差率");

        List<List<String>> contents = new ArrayList<>();
        List<String> ct = new ArrayList<>();
        ct.add(info.getSymbol());
        ct.add(ExchangeEnum.getEnum(info.getExchangeId()).name());

        // 量比
        {
            BigDecimal n = MathUtil.div(kline.getVolume(), info.getQtyAvgSmoothVolume());
            ct.add(MathUtil.stripRate(n));
        }

        // 价格
        ct.add(MathUtil.strip(kline.getClose(), infoRaw.getPricePrecision()));

        // 振幅 - |H - L| / O
        {
            BigDecimal n = MathUtil.of(kline.getHigh()).sub(kline.getLow()).abs().div(kline.getOpen()).to();
            ct.add(MathUtil.stripRate(n));
        }

        // 平均振幅
        ct.add(MathUtil.stripRate(info.getAvgPriceVolatilityRate()));

        // 交易额
        {
            BigDecimal total = kline.getQuoteVolume();
            BigDecimal buy = kline.getTakerBuyQuoteVolume();
            BigDecimal sell = MathUtil.sub(total, buy);
            BigDecimal diff = MathUtil.sub(buy, sell);

            // 成交额差值: dff = buy - sell
            ct.add(StrUtil.format("<span style=\"color: {};\">{}</span>", MathUtil.isPositive(diff) ? "green" : "red", MathUtil.stripMoney(diff)));

            // 买入额主动
            ct.add(MathUtil.stripMoney(buy));

            // 卖出额主动
            ct.add(MathUtil.stripMoney(sell));

            // 成交额
            ct.add(MathUtil.stripMoney(total));

            // 成交额平均
            ct.add(MathUtil.stripMoney(info.getQtyAvgVolumeQuote()));
        }

        // 成交量
        {
            // 成交量当前
            ct.add(MathUtil.strip(kline.getVolume(), infoRaw.getQuantityPrecision()));

            // 成交量平均
            ct.add(MathUtil.strip(info.getQtyAvgSmoothVolume(), infoRaw.getQuantityPrecision()));

            // 成交量标准差
            ct.add(MathUtil.strip(info.getQtyStdevVolume(), infoRaw.getQuantityPrecision()));

            // 成交量标准差率
            ct.add(MathUtil.stripRate(info.getQtyStdevVolumeRate()));
        }

        contents.add(ct);

//        String extend = "成交量标准差率 = 标准差 / 平均值 (衡量波动幅度)";
        String extend = "===================================================================================";

        return serviceContext.getHtmlContentBuilder().table(tilte, headers, contents, extend);
    }
}
