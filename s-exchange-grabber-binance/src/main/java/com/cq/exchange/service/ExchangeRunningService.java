package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.enums.ExchangeActionType;
import com.cq.exchange.task.*;
import com.cq.exchange.vo.ExchangeRunningParam;
import info.bitrich.xchangestream.core.StreamingExchange;
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
    private ConcurrentHashMap<ExchangeRunningParam, List<Pair<Future, ExchangeContext>>> mapRunning = new ConcurrentHashMap<>();

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

        List<Pair<Future, ExchangeContext>> futures = new ArrayList<>();
        ExchangeRunningParam.Action a = p.getAction();
        if (ExchangeActionType.OrderBook.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new OrderBookGrabber(serviceContext, exchangeContext, rabbitTemplate, a.getSymbols(), !init).init());
            futures.add(Pair.of(f, exchangeContext));
        }
        if (ExchangeActionType.AggTrade.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new AggTradeGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            futures.add(Pair.of(f, exchangeContext));
        }
        if (ExchangeActionType.ForceOrder.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new ForceOrderGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            futures.add(Pair.of(f, exchangeContext));
        }
        if (ExchangeActionType.TakerLongShortRatio.is(a.getName())) {
            a.getParams().forEach(ap -> {
                a.getSymbols().forEach(s -> {
                    Future f = threadPoolTaskScheduler.schedule(new TakerLongShortRatioGrabber(serviceContext, exchangeContext, s, ap), new CronTrigger(TakerLongShortRatioGrabber.cron(ap)));
                    futures.add(Pair.of(f, exchangeContext));
                });
            });
        }
        if (ExchangeActionType.AllTicker.is(a.getName())) {
            Future f = threadPoolTaskScheduler.submit(new AllTickerGrabber(serviceContext, exchangeContext).init());
            futures.add(Pair.of(f, exchangeContext));
        }
        if (ExchangeActionType.KLine.is(a.getName())) {
            KLineGrabber g = new KLineGrabber(threadPoolTaskScheduler, serviceContext, exchangeContext, a.getParams().get(0));
            Future f = threadPoolTaskScheduler.submit(g);
            futures.add(Pair.of(f, exchangeContext));
        }
        if (ExchangeActionType.CoinInfoRaw.is(a.getName())) {
            CoinInfoRawGrabber g = new CoinInfoRawGrabber(serviceContext, exchangeContext).init();
            // run on startup
            threadPoolTaskScheduler.submit(g);
            // run in schedule
            Future f = threadPoolTaskScheduler.schedule(g, new CronTrigger(g.cron()));
            futures.add(Pair.of(f, exchangeContext));
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

                StreamingExchange exchange = f.getValue().getExchangeCurrentStream();
                if (exchange != null) {
                    exchange.disconnect().blockingAwait();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}