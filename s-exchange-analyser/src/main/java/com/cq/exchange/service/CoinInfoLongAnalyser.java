package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.math.MathUtil;
import com.cq.exchange.entity.ExchangeAggTrade;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CoinInfoLongAnalyser implements Runnable {

    private final ServiceContext serviceContext;

    private final ExchangeEnum exchangeEnum;
    private final ExchangeTradeType tradeType;
    private final String symbol;

    private final static int RECENT_SIZE_AGGTRADE = 100_000;

    public CoinInfoLongAnalyser init() {

        return this;
    }

    public static String cron() {
        return "0 5 0/4 * * ?";
    }

    @Override
    public void run() {
        List<ExchangeAggTrade> trades = serviceContext.getExchangeAggTradeService().findRecently(exchangeEnum.getCode(), tradeType.getCode(), symbol, RECENT_SIZE_AGGTRADE);
        if (CollUtil.isEmpty(trades)) {
            return;
        }

        DescriptiveStatistics stat = new DescriptiveStatistics();

    }
}
