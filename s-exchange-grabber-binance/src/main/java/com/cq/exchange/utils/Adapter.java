package com.cq.exchange.utils;

import com.cq.exchange.entity.ExchangeKline;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;

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

}
