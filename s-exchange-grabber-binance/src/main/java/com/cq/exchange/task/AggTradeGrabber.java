package com.cq.exchange.task;

import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeAggTrade;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.service.ServiceContext;
import info.bitrich.xchangestream.binance.old.BinanceFutureStreamingMarketDataService;
import info.bitrich.xchangestream.core.ProductSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.dto.marketdata.BinanceAggTrades;

import java.util.List;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
public class AggTradeGrabber implements Grabber {

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;
    private final List<String> symbols;

    public Grabber init() {
        // subscript
        ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
        symbols.forEach(s -> builder.addAggTrades(BinanceAdapters.adaptSymbol(s, true)));
        ProductSubscription subscription = builder.build();

        exchangeContext.getExchangeCurrentStream().connect(subscription).blockingAwait();
        return this;
    }

    @Override
    public void run() {
        symbols.forEach(s -> {
            BinanceFutureStreamingMarketDataService service = (BinanceFutureStreamingMarketDataService) exchangeContext.getExchangeCurrentStream().getStreamingMarketDataService();
            service.getAggTrade(BinanceAdapters.adaptSymbol(s, true)).subscribe(
                    e -> serviceContext.getExchangeAggTradeService().save(adapt(s, e)),
                    throwable -> log.error("ERROR in getting agg trades: ", throwable)
            );
        });
    }

    @Override
    public void close() {
        exchangeContext.getExchangeCurrentStream().disconnect().blockingAwait();
    }

    private ExchangeAggTrade adapt(String s, BinanceAggTrades e) {
        ExchangeAggTrade ee = new ExchangeAggTrade();
        ee.setExchangeId(ExchangeEnum.BINANCE.getCode());
        ee.setTradeType(exchangeContext.getTradeType().getCode());
        ee.setSymbol(s);

        ee.setTradeId(e.aggregateTradeId);
        ee.setTime(e.timestamp);

        ee.setPrice(e.price.doubleValue());
        ee.setQuantity(e.quantity.doubleValue());
        ee.setBuyerMaker(e.buyerMaker);

        return ee;
    }
}
