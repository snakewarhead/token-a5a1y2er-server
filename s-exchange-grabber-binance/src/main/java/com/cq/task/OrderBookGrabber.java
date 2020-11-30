package com.cq.task;

import com.cq.ExchangeContext;
import com.cq.entity.ExchangeOrderBook;
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

    private final ExchangeContext exchangeContext;
    private final List<String> symbols;

    @Override
    public void run() {
        symbols.forEach(s -> {
            exchangeContext.getStreamingExchangeCurrent().getStreamingMarketDataService().getOrderBook(BinanceAdapters.adaptSymbol(s)).subscribe(
                    orderBook -> {
                        DiffOrderBook diffOrderBook = (DiffOrderBook) orderBook;

                        exchangeContext.getExchangeOrderBookService().save(adapt(s, diffOrderBook));

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
        b.setExchangeId(1);
        b.setTradeType(exchangeContext.getTradeType().getCode());
        b.setSymbol(symbol);
        b.setTime(o.getTimeStamp());

        b.setBids(o.getBids().stream()
                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitPrice(), limitOrder.getOriginalAmount()))
                .collect(Collectors.toList()));
        b.setAsks(o.getAsks().stream()
                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitPrice(), limitOrder.getOriginalAmount()))
                .collect(Collectors.toList()));

        // save update also
        b.setBidsUpdate(o.getBidsUpdate().stream()
                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitOrder().getLimitPrice(), limitOrder.getTotalVolume()))
                .collect(Collectors.toList()));
        b.setAsksUpdate(o.getAsksUpdate().stream()
                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitOrder().getLimitPrice(), limitOrder.getTotalVolume()))
                .collect(Collectors.toList()));

        return b;
    }
}
