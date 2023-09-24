package com.cq.exchange.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.entity.ExchangeKline;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.service.ServiceContext;
import com.cq.exchange.utils.Adapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceFuturesExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.service.BinanceFuturesMarketDataServiceRaw;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
public class KLineGrabber implements Runnable {

    private final static int INTERVAL_IN_REST = 500; // 2400 weights per minute
    private final static int INTERVAL_IN_FINAL = 5000;
    private final static int MAX = 1000;    // weight - 5
    private final static int MIN = 10;      // weight - 1
    private final static int NUM_THREADS = 2;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;
    private final ExchangeContext exchangeContextNew;
    private final ExchangePeriodEnum periodEnum;

    private BinanceFuturesMarketDataServiceRaw binanceFuturesMarketDataServiceRaw;

    private info.bitrich.xchangestream.binance.BinanceStreamingExchange exchangeNew;
    private info.bitrich.xchangestream.binance.BinanceStreamingMarketDataService binanceStreamingMarketDataServiceNew;

    public KLineGrabber(ThreadPoolTaskScheduler threadPoolTaskScheduler, ServiceContext serviceContext, ExchangeContext exchangeContext, ExchangeContext exchangeContextNew, String period) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.serviceContext = serviceContext;
        this.exchangeContext = exchangeContext;
        this.exchangeContextNew = exchangeContextNew;
        this.periodEnum = ExchangePeriodEnum.getEnum(period);

        BinanceFuturesExchange exchange = (BinanceFuturesExchange) exchangeContext.getExchangeCurrent();
        binanceFuturesMarketDataServiceRaw = (BinanceFuturesMarketDataServiceRaw) exchange.getMarketDataService();

        exchangeNew = exchangeContextNew.getExchangeNew();
        binanceStreamingMarketDataServiceNew = exchangeNew.getStreamingMarketDataService();
    }

    @Override
    public void run() {
        try {
            List<ExchangeCoinInfoRaw> ls = serviceContext.getExchangeCoinInfoRawService().find(exchangeContext.getExchangeEnum().getCode(), exchangeContext.getTradeType().getCode(), 1);
            List<List<ExchangeCoinInfoRaw>> ps = ListUtil.partition(ls, NUM_THREADS);
            for (var p : ps) {
                threadPoolTaskScheduler.submit(new ActionSub(p));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @RequiredArgsConstructor
    final class ActionSub implements Runnable {

        final List<ExchangeCoinInfoRaw> infos;

        void klineFromHttp(int limit, boolean needRest) {
            for (ExchangeCoinInfoRaw i : infos) {
                try {
                    List<BinanceKline> klines = binanceFuturesMarketDataServiceRaw.klines(i.getSymbol(), KlineInterval.getEnum(periodEnum.getSymbol()), limit, null, null);
                    List<ExchangeKline> klinesAdapt = klines.stream().map(kline -> Adapter.adaptKline(kline, exchangeContext.getExchangeEnum(), exchangeContext.getTradeType(), periodEnum)).collect(Collectors.toList());
                    if (CollUtil.isEmpty(klinesAdapt)) {
                        log.error("klines is empty {}, limit - {}", i.getSymbol(), limit);
                        continue;
                    }
                    serviceContext.getExchangeKlineService().saveAll(klinesAdapt);

                    // get each kline with sleeping a while on first time
                    if (needRest) {
                        ThreadUtil.sleep(INTERVAL_IN_REST);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        @Override
        public void run() {
            // more history of kline from http api
            klineFromHttp(MAX, true);

            // latest kline from websocket api
            exchangeNew.connect(Adapter.adaptKlineSubscription(infos, exchangeContextNew.getTradeType(), periodEnum)).blockingAwait();
            for (ExchangeCoinInfoRaw i : infos) {
                binanceStreamingMarketDataServiceNew
                        .getKlines(BinanceAdapters.adaptSymbol(i.getSymbol(), exchangeContextNew.getTradeType().isFuture()), KlineInterval.getEnum(periodEnum.getSymbol()))
                        .subscribe(kl -> {
                            ExchangeKline klAdapt = Adapter.adaptKline(kl, exchangeContext.getExchangeEnum(), exchangeContext.getTradeType(), periodEnum);
                            serviceContext.getExchangeKlineService().updateOne(klAdapt);
                        });
            }

            // make sure that the klines are continuous
            ThreadUtil.sleep(INTERVAL_IN_FINAL);
            klineFromHttp(MIN, false);

            ThreadUtil.sleep(Integer.MAX_VALUE);
        }
    }
}
