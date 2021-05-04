package com.cq.exchange.task;

import com.cq.exchange.ExchangeContext;
import com.cq.exchange.enums.ExchangeTradeType;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.ProductSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceAdapters;
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
            p = new ObjectMapper().readValue(params, ExchangeRunningParam.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (p == null) {
            throw new RuntimeException("must Set cmd line params");
        }

        if (p.getType() == ExchangeTradeType.SPOT.getCode()) {
            exchangeContext.exchangeSpot();
        } else if (p.getType() == ExchangeTradeType.FUTURE_USDT.getCode()) {
            exchangeContext.exchangeFutureUSDT();
        } else if (p.getType() == ExchangeTradeType.FUTURE_COIN.getCode()) {
            exchangeContext.exchangeFutureCoin();
        } else {
            throw new RuntimeException("exchange is null");
        }

        // subscript
        ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
        p.getActions().forEach(a -> {
            if ("OrderBook".equals(a.getName())) {
                a.getSymbols().forEach(s -> {
                    builder.addOrderbook(BinanceAdapters.adaptSymbol(s));
                });
            }
        });
        ProductSubscription subscription = builder.build();

        exchangeContext.getStreamingExchangeCurrent().connect(subscription).blockingAwait();

        // start runner&task
        p.getActions().forEach(a -> {
            if ("OrderBook".equals(a.getName())) {
                threadPoolTaskScheduler.submit(new OrderBookGrabber(exchangeContext, a.getSymbols()));
            } else if ("TakerLongShortRatio".equals(a.getName())) {
                TakerLongShortRatioGrabber grabber = new TakerLongShortRatioGrabber(exchangeContext, a.getSymbols());
                a.getParams().forEach(i -> threadPoolTaskScheduler.schedule(grabber, new CronTrigger(grabber.cron(i))));
            }
        });
    }
}
