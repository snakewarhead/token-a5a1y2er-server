package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.enums.ExchangeActionType;
import com.cq.exchange.task.*;
import com.cq.exchange.vo.ExchangeRunningParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final RabbitTemplate rabbitTemplate;

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private ConcurrentHashMap<ExchangeRunningParam, List<Pair<Future, Grabber>>> mapRunning = new ConcurrentHashMap<>();

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

        ExchangeContext exchangeContext = new ExchangeContext(p.getExchange(), p.getTradeType());

        List<Pair<Future, Grabber>> futures = new ArrayList<>();
        ExchangeRunningParam.Action a = p.getAction();
        if (ExchangeActionType.OrderBook.is(a.getName())) {
            Grabber g = new OrderBookGrabber(serviceContext, exchangeContext, rabbitTemplate, a.getSymbols(), !init).init();
            Future f = threadPoolTaskScheduler.submit(g);
            futures.add(Pair.of(f, g));
        }
        if (ExchangeActionType.AggTrade.is(a.getName())) {
            Grabber g = new AggTradeGrabber(serviceContext, exchangeContext, a.getSymbols()).init();
            Future f = threadPoolTaskScheduler.submit(g);
            futures.add(Pair.of(f, g));
        }
        if (ExchangeActionType.ForceOrder.is(a.getName())) {
            Grabber g = new ForceOrderGrabber(serviceContext, exchangeContext, a.getSymbols()).init();
            Future f = threadPoolTaskScheduler.submit(g);
            futures.add(Pair.of(f, g));
        }
        if (ExchangeActionType.TakerLongShortRatio.is(a.getName())) {
            a.getParams().forEach(ap -> {
                a.getSymbols().forEach(s -> {
                    Grabber g = new TakerLongShortRatioGrabber(serviceContext, exchangeContext, s, ap);
                    Future f = threadPoolTaskScheduler.schedule(g, new CronTrigger(TakerLongShortRatioGrabber.cron(ap)));
                    futures.add(Pair.of(f, g));
                });
            });
        }
        if (ExchangeActionType.AllTicker.is(a.getName())) {
            Grabber g = new AllTickerGrabber(serviceContext, exchangeContext).init();
            Future f = threadPoolTaskScheduler.submit(g);
            futures.add(Pair.of(f, g));
        }
        if (ExchangeActionType.KLine.is(a.getName())) {
            KLineGrabber g = new KLineGrabber(threadPoolTaskScheduler, serviceContext, exchangeContext, a.getParams().get(0));
            // run on startup
            threadPoolTaskScheduler.submit(g);
            // run in schedule
            Future f = threadPoolTaskScheduler.schedule(g, new CronTrigger(g.cron()));
            futures.add(Pair.of(f, g));
        }
        if (ExchangeActionType.CoinInfoRaw.is(a.getName())) {
            CoinInfoRawGrabber g = new CoinInfoRawGrabber(serviceContext, exchangeContext).init();
            // run on startup
            threadPoolTaskScheduler.submit(g);
            // run in schedule
            Future f = threadPoolTaskScheduler.schedule(g, new CronTrigger(g.cron()));
            futures.add(Pair.of(f, g));
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

        var futures = mapRunning.remove(p);
        if (CollUtil.isEmpty(futures)) {
            return;
        }

        futures.forEach(f -> {
            try {
                f.getKey().cancel(true);
                f.getValue().close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}