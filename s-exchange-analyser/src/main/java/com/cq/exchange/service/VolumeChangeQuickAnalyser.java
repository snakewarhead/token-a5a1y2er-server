package com.cq.exchange.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.cq.core.vo.MailMsg;
import com.cq.exchange.entity.ExchangeCoinInfo;
import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.entity.ExchangeKline;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.cq.util.MathUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public VolumeChangeQuickAnalyser init() {
        periodStaleTolerance = periodEnum.getMillis() * PERIOD_STALE_TOLERANCE;
        return this;
    }

    public String cron() throws Exception {
        if ("5m".equals(periodEnum.getSymbol())) {
            return "30 0/1 * * * ?";
        }
        throw new Exception(StrUtil.format("period not support - {}", periodEnum.getSymbol()));
    }

    @Override
    public void run() {
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

                    BigDecimal smoothVolumeMultiple = MathUtil.mul(info.getQtyAvgSmoothVolume(), multipleChange);
                    if (kline.getVolume().compareTo(smoothVolumeMultiple) < 0) {
                        // no over
                        continue;
                    }

                    // notify
                    String ct = htmlTable(DateUtil.date(kline.getOpenTime()).toString(), s, info, kline);
                    MailMsg msg = MailMsg.builder()
                            .subject("volume change quick")
                            .text(ct)
                            .silent(MailMsg.Silent.builder()
                                    .hash(SecureUtil.md5(info.getSymbol()))
                                    .deadline(TIME_STALE_NOTIFY)
                                    .build())
                            .build();
                    serviceContext.getMailClient().send(msg);

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String htmlTable(String tilte, ExchangeCoinInfoRaw infoRaw, ExchangeCoinInfo info, ExchangeKline kline) {
        // open time
        // symbol exchange
        List<String> headers = new ArrayList<>();
        headers.add("symbol");
        headers.add("exchange");
        headers.add("量比");
        headers.add("价格");
        headers.add("振幅");
        headers.add("平均振幅");
        headers.add("买入额主动(U)");
        headers.add("成交额(U)");
        headers.add("成交额平均(U)");
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

        // 买入额主动(U)
        ct.add(MathUtil.strip(kline.getTakerBuyQuoteVolume(), 2));

        // 成交额(U)
        ct.add(MathUtil.strip(kline.getQuoteVolume(), 2));

        // 成交额平均(U)
        ct.add(MathUtil.strip(info.getQtyAvgVolumeQuote(), 2));

        // 成交量
        ct.add(MathUtil.strip(kline.getVolume(), infoRaw.getQuantityPrecision()));

        // 成交量平均
        ct.add(MathUtil.strip(info.getQtyAvgSmoothVolume(), infoRaw.getQuantityPrecision()));

        // 成交量标准差
        ct.add(MathUtil.strip(info.getQtyStdevVolume(), infoRaw.getQuantityPrecision()));

        // 成交量标准差率
        ct.add(MathUtil.stripRate(info.getQtyStdevVolumeRate()));

        contents.add(ct);

        String extend = "成交量标准差率 = 标准差 / 平均值";

        return serviceContext.getHtmlContentBuilder().table(tilte, headers, contents, extend);
    }

}
