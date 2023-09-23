package com.cq.exchange.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.entity.ExchangeKline;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.service.ServiceContext;
import com.cq.exchange.utils.Adapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final static int INTERVAL_IN_MAX = 500; // 2400 weights per minute
    private final static int MAX = 1000;    // weight - 5
    private final static int MIN = 99;      // weight - 1
    private final static int NUM_THREADS = 2;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;
    private final ExchangeContext exchangeContextNew;
    private final ExchangePeriodEnum periodEnum;

    private BinanceFuturesMarketDataServiceRaw binanceFuturesMarketDataServiceRaw;

    public KLineGrabber(ThreadPoolTaskScheduler threadPoolTaskScheduler, ServiceContext serviceContext, ExchangeContext exchangeContext, ExchangeContext exchangeContextNew, String period) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.serviceContext = serviceContext;
        this.exchangeContext = exchangeContext;
        this.exchangeContextNew = exchangeContextNew;
        this.periodEnum = ExchangePeriodEnum.getEnum(period);

        BinanceFuturesExchange exchange = (BinanceFuturesExchange) exchangeContext.getExchangeCurrent();
        binanceFuturesMarketDataServiceRaw = (BinanceFuturesMarketDataServiceRaw) exchange.getMarketDataService();
    }

    public String cron() {
        if ("m".equals(periodEnum.getUnit())) {
            return StrUtil.format("2 0/{} * * * ?", periodEnum.getNum());
        }
        return null;
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
        private boolean first = true;

        @Override
        public void run() {
            // get each kline with sleeping a while on first time
            for (ExchangeCoinInfoRaw i : infos) {
                try {
                    int limit = first ? MAX : MIN;

                    List<BinanceKline> klines = binanceFuturesMarketDataServiceRaw.klines(i.getSymbol(), KlineInterval.getEnum(periodEnum.getSymbol()), limit, null, null);
                    List<ExchangeKline> klinesAdapt = klines.stream().map(kline -> Adapter.adaptKline(kline, exchangeContext.getExchangeEnum(), exchangeContext.getTradeType(), periodEnum)).collect(Collectors.toList());
                    if (CollUtil.isEmpty(klinesAdapt)) {
                        continue;
                    }
                    serviceContext.getExchangeKlineService().saveAll(klinesAdapt);

                    if (first) {
                        ThreadUtil.sleep(INTERVAL_IN_MAX);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            first = false;
        }
    }
}
