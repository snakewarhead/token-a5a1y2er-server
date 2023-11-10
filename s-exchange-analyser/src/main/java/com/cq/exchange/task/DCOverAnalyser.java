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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class DCOverAnalyser implements Runnable {
    private final ServiceContext serviceContext;
    private final ExchangeEnum exchangeEnum;
    private final ExchangeTradeType tradeType;
    private final ExchangePeriodEnum periodEnum;

    /**
     * highest or lowest price in the length of kline.
     * </p>
     * every 8 hours is a period. i.e. if kline period is 5m, then the length is 8 * 60 / 5 = 96
     */
    private final int lengthDC;

    private final static long TIME_STALE_NOTIFY = 4 * 3600 * 1000;

    private long periodStaleTolerance;
    private NotifyContext notifyContext;

    public DCOverAnalyser init() {
        periodStaleTolerance = periodEnum.getMillis() * serviceContext.PERIOD_STALE_TOLERANCE;
        notifyContext = new NotifyContext(TIME_STALE_NOTIFY);
        return this;
    }

    public String cron() throws Exception {
        if ("m".equals(periodEnum.getUnit())) {
            return StrUtil.format("3 0/{} * * * ?", periodEnum.getNum());
        }
        throw new Exception(StrUtil.format("cron not support - {}", periodEnum.getSymbol()));
    }

    @Override
    public void run() {
        StopWatch sw = new StopWatch();
        sw.start(StrUtil.format("{}", this.getClass().getName()));

        Date now = DateUtil.date();
        try {
            List<ExchangeCoinInfoRaw> symbols = serviceContext.getExchangeCoinInfoRawService().find(exchangeEnum.getCode(), tradeType.getCode(), 1);
            for (var s : symbols) {
                try {
                    // get highest or lowest price in the klines
                    ExchangeCoinInfo info = serviceContext.getExchangeCoinInfoService().find(exchangeEnum.getCode(), tradeType.getCode(), s.getSymbol(), periodEnum.getSymbol());
                    if (info == null) {
                        log.error("ExchangeCoinInfo is not found. {}", s.getSymbol());
                        continue;
                    }

                    long diffInfo = DateUtil.betweenMs(info.getDateUpdate(), now);
                    if (diffInfo > periodStaleTolerance) {
                        log.error("ExchangeCoinInfo is stale. {}, stale time: {}", s.getSymbol(), diffInfo);
                        continue;
                    }

                    // get last - 1 kline close price
                    ExchangeKline kline = serviceContext.getExchangeKlineService().findLatestPre(exchangeEnum.getCode(), tradeType.getCode(), s.getSymbol(), periodEnum.getSymbol());
                    if (kline == null) {
                        log.error("kline is not found. {}", s.getSymbol());
                        continue;
                    }

                    // last - 1
                    long diffKline = DateUtil.betweenMs(kline.getDateUpdate(), now);
                    if (diffKline > 2 * periodEnum.getMillis()) {
                        log.error("kline is stale. {}, stale time: {}", s.getSymbol(), diffKline);
                        continue;
                    }

                    // is over
                    ExchangeCoinInfo.PricesVolatility pricesVolatility = info.getPricesVolatilities().get(lengthDC);
                    if (pricesVolatility == null) {
                        continue;
                    }

                    boolean isOverHigh = kline.getClose().compareTo(pricesVolatility.getPriceHigh()) > 0;
                    boolean isOverLow = kline.getClose().compareTo(pricesVolatility.getPriceLow()) < 0;
                    if (!isOverHigh && !isOverLow) {
                        continue;
                    }

                    // notify
                    if (notifyContext.fresh(s.getSymbol())) {
                        String ct = htmlTables(DateUtil.date(kline.getOpenTime()).toString(), s, info, kline, isOverHigh);
                        MailMsg msg = MailMsg.builder()
                                .subject(StrUtil.format("dc over {} - length: {}", periodEnum.getSymbol(), lengthDC))
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

    private String htmlTables(String title, ExchangeCoinInfoRaw infoRaw, ExchangeCoinInfo info, ExchangeKline kline, boolean isOverHigh) {
        List<String> headers = new ArrayList<>();
        headers.add("symbol");
        headers.add("close");
        headers.add("highest");
        headers.add("lowest");

        List<List<String>> contents = new ArrayList<>();
        List<String> ct = new ArrayList<>();

        // symbol
        ct.add(info.getSymbol());

        // close
        ct.add(StrUtil.format("<span style=\"color: {};\">{}</span>", isOverHigh ? "green" : "red", MathUtil.strip(kline.getClose(), infoRaw.getPricePrecision())));

        {
            ExchangeCoinInfo.PricesVolatility pricesVolatility = info.getPricesVolatilities().get(lengthDC);
            // highest
            ct.add(MathUtil.strip(pricesVolatility.getPriceHigh(), infoRaw.getPricePrecision()));
            // lowest
            ct.add(MathUtil.strip(pricesVolatility.getPriceLow(), infoRaw.getPricePrecision()));
        }
        contents.add(ct);

        String extend = "=================";

        return serviceContext.getHtmlContentBuilder().table(title, headers, contents, extend);
    }

}
