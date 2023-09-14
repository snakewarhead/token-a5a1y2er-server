package com.cq.exchange.task;

import cn.hutool.core.thread.ThreadUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.service.ServiceContext;
import info.bitrich.xchangestream.core.ProductSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AllTickerGrabber implements Runnable {

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;

    public Runnable init() {
        // subscript
        ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
        builder.setAllTicker(true);
        ProductSubscription subscription = builder.build();

        exchangeContext.getExchangeCurrentStream().connect(subscription).blockingAwait();
        return this;
    }

    @Override
    public void run() {
        exchangeContext.getExchangeCurrentStream().getStreamingMarketDataService().getAllTicker().subscribe(t -> {
            log.info("{}", t);
        });
    }
}
