package com.cq.exchange.task;

import cn.hutool.core.thread.ThreadUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.vo.ExchangeRunningParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExchangeInitial implements ApplicationRunner {

    @Autowired
    private ExchangeContext exchangeContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        int poolSize = 8;
        try {
            poolSize = Integer.parseInt(args.getOptionValues("threadPoolSize").get(0));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.initialize();

        ExchangeRunningParam p = null;
        try {
            String params = args.getOptionValues("params").get(0);
            p = ExchangeRunningParam.parse(params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (p == null) {
            throw new RuntimeException("must Set cmd line params");
        }

        exchangeContext.initExchange(p.getType());

        // start runner&task
        p.getActions().forEach(a -> {
            if ("OrderBook".equals(a.getName())) {
                threadPoolTaskScheduler.submit(new OrderBookGrabber(exchangeContext, a.getSymbols()).init());
            } else if ("AggTrade".equals(a.getName())) {
                threadPoolTaskScheduler.submit(new AggTradeGrabber(exchangeContext, a.getSymbols()).init());
            } else if ("ForceOrder".equals(a.getName())) {
                threadPoolTaskScheduler.submit(new ForceOrderGrabber(exchangeContext, a.getSymbols()).init());
            } else if ("TakerLongShortRatio".equals(a.getName())) {
                TakerLongShortRatioGrabber grabber = new TakerLongShortRatioGrabber(exchangeContext, a.getSymbols());
                a.getParams().forEach(i -> threadPoolTaskScheduler.schedule(grabber, new CronTrigger(grabber.cron(i))));
            }
        });

        ThreadUtil.waitForDie(Thread.currentThread());
    }
}
