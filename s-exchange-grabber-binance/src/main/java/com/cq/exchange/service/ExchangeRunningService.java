package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.enums.ExchangeActionType;
import com.cq.exchange.enums.ExchangeTradeType;
import com.cq.exchange.task.*;
import com.cq.exchange.vo.ExchangeRunningParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeRunningService {

    private final ServiceContext serviceContext;

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private ConcurrentHashMap<ExchangeRunningParam, List<Future>> mapRunning = new ConcurrentHashMap<>();

    public void init(int poolSize) {
        threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.initialize();
    }

    public void start(ExchangeRunningParam p, boolean init) {
        if (mapRunning.containsKey(p)) {
            log.warn("start - Already running - {}", p.toString());
            return;
        }

        List<Future> futures = new ArrayList<>();
        ExchangeContext exchangeContext = new ExchangeContext(p.getExchange(), p.getTradeType());
        ExchangeRunningParam.Action a = p.getAction();
        if (ExchangeActionType.OrderBook.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new OrderBookGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            futures.add(f);
        }
        if (ExchangeActionType.AggTrade.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new AggTradeGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            futures.add(f);
        }
        if (ExchangeActionType.ForceOrder.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new ForceOrderGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            futures.add(f);
        }
        if (ExchangeActionType.TakerLongShortRatio.is(a.getName())) {
            a.getParams().forEach(ap -> {
                a.getSymbols().forEach(s -> {
                    Future f = threadPoolTaskScheduler.schedule(new TakerLongShortRatioGrabber(serviceContext, exchangeContext, s, ap), new CronTrigger(TakerLongShortRatioGrabber.cron(ap)));
                    futures.add(f);
                });
            });
        }
        if (ExchangeActionType.AllTicker.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new AllTickerGrabber(serviceContext, exchangeContext).init());
            futures.add(f);
        }

        if (!init) {
            mapRunning.put(p, futures);
        }
    }

    public void stop(ExchangeRunningParam p) {
        if (!mapRunning.containsKey(p)) {
            log.warn("stop - no running - {}", p.toString());
            return;
        }

        List<Future> futures = mapRunning.remove(p);
        if (CollUtil.isEmpty(futures)) {
            return;
        }

        futures.stream().forEach(f -> {
            try {
                f.cancel(true);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}