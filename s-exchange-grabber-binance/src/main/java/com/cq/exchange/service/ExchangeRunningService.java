package com.cq.exchange.service;

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

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeRunningService {

    private final ServiceContext serviceContext;

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public void init(int poolSize) {
        threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.initialize();
    }

    public void start(ExchangeRunningParam p) {
        ExchangeContext exchangeContext = new ExchangeContext(p.getTradeType());

        p.getActions().forEach(a -> {
            if (ExchangeRunningParam.ActionType.OrderBook.equals(a.getName())) {
                threadPoolTaskScheduler.submit(new OrderBookGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            } else if (ExchangeRunningParam.ActionType.AggTrade.equals(a.getName())) {
                threadPoolTaskScheduler.submit(new AggTradeGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            } else if (ExchangeRunningParam.ActionType.ForceOrder.equals(a.getName())) {
                threadPoolTaskScheduler.submit(new ForceOrderGrabber(serviceContext, exchangeContext, a.getSymbols()).init());
            } else if (ExchangeRunningParam.ActionType.TakerLongShortRatio.equals(a.getName())) {
                TakerLongShortRatioGrabber grabber = new TakerLongShortRatioGrabber(serviceContext, exchangeContext, a.getSymbols());
                a.getParams().forEach(i -> threadPoolTaskScheduler.schedule(grabber, new CronTrigger(grabber.cron(i))));
            }
        });

    }
}
