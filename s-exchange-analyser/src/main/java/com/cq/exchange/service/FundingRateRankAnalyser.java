package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.cq.exchange.entity.ExchangeFutureFundingRate;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class FundingRateRankAnalyser implements Runnable {

    private final ServiceContext serviceContext;
    private final long timeQuery;

    private final static long TIME_STALE = 8 * 3600 * 1000;
    private static ConcurrentHashMap<Integer, Long> stales;

    private final static BigDecimal rateLimit = BigDecimal.valueOf(0.1);

    private final static int NUM_RANK = 10;
    private final static int TYPE_TRADE = ExchangeTradeType.FUTURE_USDT.getCode();
    private final static int[] IDS_EXCHANGE = {
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
        boolean notify = false;

        // rate negative
        List<ExchangeFutureFundingRate> lsNegative = serviceContext.getExchangeFutureFundingRateSerivce().findInRateRank(IDS_EXCHANGE, TYPE_TRADE, timeQuery, NUM_RANK, 1);
        notify = needNotify(lsNegative);

        // rate positive
        List<ExchangeFutureFundingRate> lsPositive = serviceContext.getExchangeFutureFundingRateSerivce().findInRateRank(IDS_EXCHANGE, TYPE_TRADE, timeQuery, NUM_RANK, -1);
        notify = needNotify(lsPositive);

        if (!notify) {
            return;
        }

        // notify in html
    }

    private boolean needNotify(List<ExchangeFutureFundingRate> ls) {
        if (CollUtil.isEmpty(ls)) {
            return false;
        }

        ExchangeFutureFundingRate r = ls.get(0);
        boolean over = r.getLastFundingRate().abs().compareTo(rateLimit) >= 0;

        int hash = Objects.hash(r.getExchangeId(), r.getTradeType(), r.getSymbol());
        Long timeStale = stales.get(hash);
        Long timeCurr = DateUtil.current(false);
        boolean fresh = timeStale == null || timeStale.compareTo(timeCurr) < 0;
        if (fresh) {
            stales.put(hash, timeCurr + TIME_STALE);
        }

        return over && fresh;
    }
}
