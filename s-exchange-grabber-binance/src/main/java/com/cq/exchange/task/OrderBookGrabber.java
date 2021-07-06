package com.cq.exchange.task;

import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeOrderBook;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.service.ServiceContext;
import info.bitrich.xchangestream.core.ProductSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.dto.marketdata.DiffOrderBook;

import java.util.List;
import java.util.stream.Collectors;

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
        symbols.forEach(s -> builder.addOrderbook(BinanceAdapters.adaptSymbol(s)));
        ProductSubscription subscription = builder.build();

        exchangeContext.getExchangeCurrentStream().connect(subscription).blockingAwait();
        return this;
    }

    @Override
    public void run() {
        symbols.forEach(s -> {
            exchangeContext.getExchangeCurrentStream().getStreamingMarketDataService().getOrderBook(BinanceAdapters.adaptSymbol(s)).subscribe(
                    orderBook -> {
                        DiffOrderBook diffOrderBook = (DiffOrderBook) orderBook;

                        serviceContext.getExchangeOrderBookService().save(adapt(s, diffOrderBook));

                        // TODO: save all in first time or DiffOrderBook.isFullUpdate == true
                        // TODO: update partial next time.

                        // TODO: send notify message to another services by amqp whether the orderupdates are comming.
                    },
                    throwable -> log.error("ERROR in getting order book: ", throwable)
            );
        });
    }

    private ExchangeOrderBook adapt(String symbol, DiffOrderBook o) {
        ExchangeOrderBook b = new ExchangeOrderBook();
        b.setExchangeId(ExchangeEnum.BINANCE.getCode());
        b.setTradeType(exchangeContext.getTradeType().getCode());
        b.setSymbol(symbol);
        b.setTime(o.getTimeStamp());

        b.setBids(o.getBids().stream()
                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitPrice(), limitOrder.getOriginalAmount()))
                .collect(Collectors.toList()));
        b.setAsks(o.getAsks().stream()
                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitPrice(), limitOrder.getOriginalAmount()))
                .collect(Collectors.toList()));

        // TODO: disable update for a while, client get the full data
        // save update also
//        b.setBidsUpdate(o.getBidsUpdate().stream()
//                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitOrder().getLimitPrice(), limitOrder.getTotalVolume()))
//                .collect(Collectors.toList()));
//        b.setAsksUpdate(o.getAsksUpdate().stream()
//                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitOrder().getLimitPrice(), limitOrder.getTotalVolume()))
//                .collect(Collectors.toList()));

        return b;
    }
}
