package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.task.AggTradeGrabber;
import com.cq.exchange.task.ForceOrderGrabber;
import com.cq.exchange.task.OrderBookGrabber;
import com.cq.exchange.task.TakerLongShortRatioGrabber;
import com.cq.exchange.vo.ExchangeRunningParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeRunningService {

    private final ServiceContext serviceContext;

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private Hashtable<ExchangeRunningParam, List<Future>> hashtableRunning = new Hashtable<>();

    public void init(int poolSize) {
        threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.initialize();
    }

    public void start(ExchangeRunningParam p, boolean init) {
        if (hashtableRunning.containsKey(p)) {
            log.warn("start - Already running - {}", p.toString());
            return;
        }

        List<Future> futures = new ArrayList<>();
        ExchangeContext exchangeContext = new ExchangeContext(p.getTradeType());
        ExchangeRunningParam.Action a = p.getAction();
        if (ExchangeRunningParam.ActionType.OrderBook.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new OrderBookGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            futures.add(f);
        } else if (ExchangeRunningParam.ActionType.AggTrade.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new AggTradeGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            futures.add(f);
        } else if (ExchangeRunningParam.ActionType.ForceOrder.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new ForceOrderGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            futures.add(f);
        } else if (ExchangeRunningParam.ActionType.TakerLongShortRatio.is(a.getName())) {
            TakerLongShortRatioGrabber grabber = new TakerLongShortRatioGrabber(serviceContext, exchangeContext, a.getSymbols());
            a.getParams().forEach(i -> {
                Future f = threadPoolTaskScheduler.schedule(grabber, new CronTrigger(grabber.cron(i)));
                futures.add(f);
            });
        } else {
            throw new RuntimeException("action type is not support");
        }

        if (!init) {
            hashtableRunning.put(p, futures);
        }
    }

    public void stop(ExchangeRunningParam p) {
        if (!hashtableRunning.containsKey(p)) {
            log.warn("stop - no running - {}", p.toString());
            return;
        }

        List<Future> futures = hashtableRunning.remove(p);
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