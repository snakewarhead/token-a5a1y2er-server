package com.cq.exchange.utils;

import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.entity.ExchangeKline;
import com.cq.exchange.entity.ExchangeOrderBook;
import com.cq.exchange.entity.ExchangeOrderBookDiff;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import info.bitrich.xchangestream.binance.KlineSubscription;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.dto.marketdata.DiffOrderBook;
import org.knowm.xchange.instrument.Instrument;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Adapter {
    private Adapter() {
    }

    public static ExchangeKline adaptKline(BinanceKline i, ExchangeEnum exchangeEnum, ExchangeTradeType tradeType, ExchangePeriodEnum periodEnum) {
        String symbol = BinanceAdapters.toSymbol(i.getInstrument());
        String pair = BinanceAdapters.adaptSymbol(symbol, false).toString();
        ExchangeKline l = ExchangeKline.builder()
                .period(periodEnum.getSymbol())
                .openTime(i.getOpenTime())
                .closeTime(i.getCloseTime())
                .open(i.getOpen())
                .high(i.getHigh())
                .low(i.getLow())
                .close(i.getClose())
                .volume(i.getVolume())
                .quoteVolume(i.getQuoteAssetVolume())
                .numberOfTrades(i.getNumberOfTrades())
                .takerBuyBaseVolume(i.getTakerBuyBaseAssetVolume())
                .takerBuyQuoteVolume(i.getTakerBuyQuoteAssetVolume())
                .build();
        l.setExchangeId(exchangeEnum.getCode());
        l.setTradeType(tradeType.getCode());
        l.setSymbol(symbol);
        l.setPair(pair);
        return l;
    }

    public static KlineSubscription adaptKlineSubscription(List<ExchangeCoinInfoRaw> infos, ExchangeTradeType tradeType, ExchangePeriodEnum periodEnum) {
        Set<KlineInterval> klineIntervals = Set.of(KlineInterval.getEnum(periodEnum.getSymbol()));
        Map<Instrument, Set<KlineInterval>> klineSubscriptionMap = infos.stream()
                .map(i -> BinanceAdapters.adaptSymbol(i.getSymbol(), tradeType.isFuture()))
                .collect(Collectors.toMap(Function.identity(), c -> klineIntervals));

        return new KlineSubscription(klineSubscriptionMap);
    }

    public static ExchangeOrderBook adaptOrderBook(String symbol, ExchangeEnum exchangeEnum, ExchangeTradeType tradeType, DiffOrderBook o) {
        ExchangeOrderBook b = new ExchangeOrderBook();
        b.setExchangeId(exchangeEnum.getCode());
        b.setTradeType(tradeType.getCode());
        b.setSymbol(symbol);
        b.setPair(BinanceAdapters.adaptSymbol(symbol, false).toString());
        b.setTime(o.getTimeStamp());

        b.setUpdateIdLast(o.getLastUpdateId());

        b.setBids(o.getBids().stream()
                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitPrice(), limitOrder.getOriginalAmount()))
                .collect(Collectors.toList()));
        b.setAsks(o.getAsks().stream()
                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitPrice(), limitOrder.getOriginalAmount()))
                .collect(Collectors.toList()));

        return b;
    }

    public static ExchangeOrderBookDiff wrapOrderBook(String symbol, ExchangeEnum exchangeEnum, ExchangeTradeType tradeType, DiffOrderBook o) {
        ExchangeOrderBookDiff b = new ExchangeOrderBookDiff();
        b.setExchangeId(exchangeEnum.getCode());
        b.setTradeType(tradeType.getCode());
        b.setSymbol(symbol);
        b.setPair(BinanceAdapters.adaptSymbol(symbol, false).toString());

        b.setUpdateIdLast(o.getLastUpdateId());
        b.setUpdateIdFirst(o.getFirstUpdateId());
        b.setUpdateIdLastLast(o.getLastLastUpdateId());

        b.setBidsUpdate(o.getBidsUpdate().stream()
                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitOrder().getLimitPrice(), limitOrder.getTotalVolume()))
                .collect(Collectors.toList()));
        b.setAsksUpdate(o.getAsksUpdate().stream()
                .map(limitOrder -> new ExchangeOrderBook.Order(limitOrder.getLimitOrder().getLimitPrice(), limitOrder.getTotalVolume()))
                .collect(Collectors.toList()));

        return b;
    }
}
