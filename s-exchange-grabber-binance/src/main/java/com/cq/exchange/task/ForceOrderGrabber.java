package com.cq.exchange.task;

import cn.hutool.core.date.DateUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeForceOrder;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.service.ServiceContext;
import info.bitrich.xchangestream.binance.BinanceFutureStreamingMarketDataService;
import info.bitrich.xchangestream.core.ProductSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.dto.trade.BinanceForceOrder;

import java.util.List;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
public class ForceOrderGrabber implements Runnable {

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;
    private final List<String> symbols;

    public Runnable init() {
        // subscript
        ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
        symbols.forEach(s -> builder.addForceOrders(BinanceAdapters.adaptSymbol(s)));
        ProductSubscription subscription = builder.build();

        exchangeContext.getExchangeCurrentStream().connect(subscription).blockingAwait();
        return this;
    }

    @Override
    public void run() {
        symbols.forEach(s -> {
            BinanceFutureStreamingMarketDataService service = (BinanceFutureStreamingMarketDataService) exchangeContext.getExchangeCurrentStream().getStreamingMarketDataService();
            service.getForceOrder(BinanceAdapters.adaptSymbol(s)).subscribe(
                    e -> serviceContext.getExchangeForceOrderService().save(adapt(s, e)),
                    throwable -> log.error("ERROR in getting force order: ", throwable)
            );
        });
    }

    private ExchangeForceOrder adapt(String s, BinanceForceOrder e) {
        ExchangeForceOrder ee = new ExchangeForceOrder();
        ee.setExchangeId(ExchangeEnum.BINANCE.getCode());
        ee.setTradeType(exchangeContext.getTradeType().getCode());
        ee.setSymbol(s);
        ee.setTime(DateUtil.date(e.time));

        ee.setPrice(e.price);
        ee.setAvragePrice(e.avragePrice);
        ee.setOrigQty(e.origQty);
        ee.setExecutedQty(e.executedQty);
        ee.setStatus(e.status.name());
        ee.setTimeInForce(e.timeInForce.name());
        ee.setType(e.type.name());
        ee.setSide(e.side.name());

        return ee;
    }
}
