package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.exchange.entity.ExchangeFutureFundingRate;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FundingRateRankAnalyser implements Runnable {

    private final ServiceContext serviceContext;
    private final long timeCurrent;

    private final static BigDecimal rateLimit = BigDecimal.valueOf(0.1);

    private final static int numRank = 20;
    private final static int typeTrade = ExchangeTradeType.FUTURE_USDT.getCode();
    private final static int[] idsExchange = {
            ExchangeEnum.BINANCE.getCode(),
            ExchangeEnum.OKX.getCode(),
            ExchangeEnum.HUOBI.getCode(),
            ExchangeEnum.GATEIO.getCode(),
    };

    public FundingRateRankAnalyser init() {
        return this;
    }

    @Override
    public void run() {
        boolean needNotify = false;

        // rate negative
        List<ExchangeFutureFundingRate> lsNegative = serviceContext.getExchangeFutureFundingRateSerivce().findInRateRank(idsExchange, typeTrade, timeCurrent, numRank, 1);
        needNotify = needNotify(lsNegative);

        // rate positive
        List<ExchangeFutureFundingRate> lsPositive = serviceContext.getExchangeFutureFundingRateSerivce().findInRateRank(idsExchange, typeTrade, timeCurrent, numRank, -1);
        needNotify = needNotify(lsPositive);

        if (!needNotify) {
            return;
        }

        // notify in html
    }

    private boolean needNotify(List<ExchangeFutureFundingRate> ls) {
        if (CollUtil.isEmpty(ls)) {
            return false;
        }
        return ls.get(0).getLastFundingRate().abs().compareTo(rateLimit) >= 0;
    }
}
