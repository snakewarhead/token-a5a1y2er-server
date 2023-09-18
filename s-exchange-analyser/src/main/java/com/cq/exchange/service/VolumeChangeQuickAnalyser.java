package com.cq.exchange.service;

import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class VolumeChangeQuickAnalyser implements Runnable {
    private final ServiceContext serviceContext;
    private final ExchangeEnum exchangeEnum;
    private final ExchangeTradeType tradeType;

    private ExchangePeriodEnum periodEnum;

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
            // all symbol trading
            List<ExchangeCoinInfoRaw> ls = serviceContext.getExchangeCoinInfoRawService().find(exchangeEnum.getCode(), tradeType.getCode(), 1);
            for (ExchangeCoinInfoRaw i : ls) {
                try {
                    // average volume of klines which will been trimed with 2X variance

                    // volume of ticker is over

                    // notify
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
