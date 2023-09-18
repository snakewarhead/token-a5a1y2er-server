package com.cq.exchange.service;

import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class VolumeChangeQuickAnalyser implements Runnable {
    private final static int LIMIT_KLINES = 1000;
    private final ServiceContext serviceContext;
    private final ExchangeEnum exchangeEnum;
    private final ExchangeTradeType tradeType;
    private ExchangePeriodEnum periodEnum;
    private Map<String, DescriptiveStatistics> mapStatistics = new HashMap<>();
    private boolean first = true;

    public VolumeChangeQuickAnalyser init(String period) {
        this.periodEnum = ExchangePeriodEnum.getEnum(period);
        return this;
    }

    public String cron() {
        if (periodEnum.is(ExchangePeriodEnum.m5)) {
            return "10 0/1 * * * ?";
        }
        return null;
    }

    @Override
    public void run() {
        try {
            int limit = first ? LIMIT_KLINES : 1;

            // all symbol trading
            List<ExchangeCoinInfoRaw> ls = serviceContext.getExchangeCoinInfoRawService().find(exchangeEnum.getCode(), tradeType.getCode(), 1);
            for (ExchangeCoinInfoRaw i : ls) {
                try {
                    serviceContext.getExchangeKlineService().findLast(exchangeEnum.getCode(), tradeType.getCode(), i.getSymbol(), periodEnum.getSymbol(), limit);
                    // average volume of klines which will be trimmed with 2X stddev of up and down

                    DescriptiveStatistics stats = mapStatistics.get(i.getSymbol());
                    if (stats == null) {
                        stats = new DescriptiveStatistics();
                        stats.setWindowSize(LIMIT_KLINES);
                    }

                    double std = stats.getStandardDeviation();

                    // volume of ticker is over

                    // notify
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            first = false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
