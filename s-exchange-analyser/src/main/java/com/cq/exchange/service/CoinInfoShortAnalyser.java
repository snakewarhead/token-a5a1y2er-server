package com.cq.exchange.service;

import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CoinInfoShortAnalyser implements Runnable {

    private final ServiceContext serviceContext;

    private final ExchangeEnum exchangeEnum;
    private final ExchangeTradeType tradeType;
    private final String symbol;

    public CoinInfoShortAnalyser init() {

        return this;
    }

    public static String cron() {
        return "0 0/5 * * * ?";
    }

    @Override
    public void run() {
        // 平均挂单量

        // 量比
    }
}
