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

    private final static long INTERVAL_IN_REST = 500L; // 2400 weights per minute
    private final static long INTERVAL_IN_FINAL = 100000L;
    private final static int MAX = 1000;    // weight - 5
    private final static int MIN = 99;      // weight - 1
    private final static int NUM_THREADS = 2;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;
    private final ExchangePeriodEnum periodEnum;

    public KLineGrabber(ThreadPoolTaskScheduler threadPoolTaskScheduler, ServiceContext serviceContext, ExchangeContext exchangeContext, String period) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.serviceContext = serviceContext;
        this.exchangeContext = exchangeContext;
        this.periodEnum = ExchangePeriodEnum.getEnum(period);
    }

    @Override
    public void run() {
        try {
            List<ExchangeCoinInfoRaw> ls = serviceContext.getExchangeCoinInfoRawService().find(exchangeContext.getExchangeEnum().getCode(), exchangeContext.getTradeType().getCode(), 1);
            if (CollUtil.isEmpty(ls)) {
                log.error("List<ExchangeCoinInfoRaw> is empty");
                return;
            }

            int size = ls.size() / NUM_THREADS + ls.size() % NUM_THREADS;
            List<List<ExchangeCoinInfoRaw>> ps = ListUtil.partition(ls, size);
            for (var p : ps) {
                // exchangeContextNew per thread
                ExchangeContext exchangeContextNew = new ExchangeContext(exchangeContext.getExchangeEnum().getCode(), exchangeContext.getTradeType().getCode(), true);
                threadPoolTaskScheduler.submit(new ActionSub((BinanceFuturesExchange) exchangeContext.getExchangeCurrent(), exchangeContextNew.getExchangeNew(), p));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @RequiredArgsConstructor
    final class ActionSub implements Runnable {

        private final BinanceFuturesExchange exchangeOld;
        final info.bitrich.xchangestream.binance.BinanceStreamingExchange exchangeNew;
        final List<ExchangeCoinInfoRaw> infos;

        void klineFromHttp(int limit, boolean needRest) {
            BinanceFuturesMarketDataServiceRaw binanceFuturesMarketDataServiceRaw = (BinanceFuturesMarketDataServiceRaw) exchangeOld.getMarketDataService();

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
            exchangeNew.connect(Adapter.adaptKlineSubscription(infos, exchangeContext.getTradeType(), periodEnum)).blockingAwait();
            for (ExchangeCoinInfoRaw i : infos) {
                exchangeNew.getStreamingMarketDataService()
                        .getKlines(BinanceAdapters.adaptSymbol(i.getSymbol(), exchangeContext.getTradeType().isFuture()), KlineInterval.getEnum(periodEnum.getSymbol()))
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
