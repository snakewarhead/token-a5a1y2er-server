package com.cq.exchange.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.service.ServiceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceFuturesExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceFuturesPremiumIndex;
import org.knowm.xchange.binance.service.BinanceFuturesMarketDataServiceRaw;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FundingRateGrabber implements Runnable {

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;

    private BinanceFuturesMarketDataServiceRaw binanceFuturesMarketDataServiceRaw;

    public static String cron(String periodStr) {
        return StrUtil.format("1 */{} * * * ?", periodStr);
    }

    public Runnable init() {
        BinanceFuturesExchange exchange = (BinanceFuturesExchange) exchangeContext.getExchangeCurrent();
        binanceFuturesMarketDataServiceRaw = (BinanceFuturesMarketDataServiceRaw) exchange.getMarketDataService();

        return this;
    }

    @Override
    public void run() {
        try {
            List<BinanceFuturesPremiumIndex> ls = binanceFuturesMarketDataServiceRaw.premiumIndexUSDTAllSymbols();
            if (CollUtil.isEmpty(ls)) {
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
