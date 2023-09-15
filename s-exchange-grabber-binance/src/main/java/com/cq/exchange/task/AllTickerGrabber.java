package com.cq.exchange.task;

import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeTicker;
import com.cq.exchange.service.ServiceContext;
import info.bitrich.xchangestream.core.ProductSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.marketdata.Ticker;

import java.util.List;
import java.util.stream.Collectors;

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
        exchangeContext.getExchangeCurrentStream().getStreamingMarketDataService().getAllTicker().subscribe(ls -> {
            List<ExchangeTicker> lst = ls.stream().map(this::adapt).collect(Collectors.toList());
            serviceContext.getExchangeTickerService().saveAll(lst);
        });
    }

    private ExchangeTicker adapt(Ticker t) {
        ExchangeTicker et = ExchangeTicker.builder()
                .open(t.getOpen())
                .last(t.getLast())
                .bid(t.getBid())
                .ask(t.getAsk())
                .high(t.getHigh())
                .low(t.getLow())
                .vwap(t.getVwap())
                .volume(t.getVolume())
                .quoteVolume(t.getQuoteVolume())
                .timestamp(t.getTimestamp())
                .bidSize(t.getBidSize())
                .askSize(t.getAskSize())
                .percentageChange(t.getPercentageChange())
                .lastVolume(t.getLastVolume())
                .count(t.getCount())
                .build();
        et.setExchangeId(exchangeContext.getExchangeEnum().getCode());
        et.setTradeType(exchangeContext.getTradeType().getCode());
        et.setSymbol(t.getInstrument().toString());
        et.setPair(t.getInstrument().toString());
        return et;
    }
}
