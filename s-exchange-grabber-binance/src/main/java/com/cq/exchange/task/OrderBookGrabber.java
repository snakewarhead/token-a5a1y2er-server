package com.cq.exchange.task;

import cn.hutool.core.util.StrUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeOrderBookDiff;
import com.cq.exchange.service.ServiceContext;
import com.cq.exchange.utils.Adapter;
import info.bitrich.xchangestream.core.ProductSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.dto.marketdata.DiffOrderBook;

import java.util.List;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
public class OrderBookGrabber implements Runnable {

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;
    private final List<String> symbols;

    public Runnable init() {
        // subscript
        ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
        symbols.forEach(s -> builder.addOrderbook(BinanceAdapters.adaptSymbol(s, true)));
        ProductSubscription subscription = builder.build();

        exchangeContext.getExchangeCurrentStream().connect(subscription).blockingAwait();
        return this;
    }

    @Override
    public void run() {
        symbols.forEach(s -> {
            exchangeContext.getExchangeCurrentStream().getStreamingMarketDataService().getOrderBook(BinanceAdapters.adaptSymbol(s, true)).subscribe(
                    orderBook -> {
                        DiffOrderBook diffOrderBook = (DiffOrderBook) orderBook;

                        serviceContext.getExchangeOrderBookService().save(Adapter.adaptOrderBook(s, exchangeContext.getExchangeEnum(), exchangeContext.getTradeType(), diffOrderBook));

                        // TODO: save all in first time or DiffOrderBook.isFullUpdate == true
                        // TODO: update partial next time.

                        // TODO: send notify message to another services by amqp whether the orderupdates are comming.
                        ExchangeOrderBookDiff wraped = Adapter.wrapOrderBook(s, exchangeContext.getExchangeEnum(), exchangeContext.getTradeType(), diffOrderBook);
                    },
                    throwable -> log.error(StrUtil.format("ERROR in getting order book: {} - ", s), throwable)
            );
        });
    }
}
