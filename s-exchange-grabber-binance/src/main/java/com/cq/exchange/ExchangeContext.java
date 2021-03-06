package com.cq.exchange;

import com.cq.exchange.enums.ExchangeTradeType;
import com.cq.exchange.service.*;
import info.bitrich.xchangestream.binance.BinanceFutureStreamingExchange;
import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchangeSpecification;
import org.knowm.xchange.binance.BinanceFuturesCoin;
import org.knowm.xchange.binance.BinanceFuturesUSDT;
import org.knowm.xchange.binance.dto.FuturesSettleType;
import org.springframework.stereotype.Component;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@Getter
public class ExchangeContext {

    private final ExchangeTradeType tradeType;

    private BaseExchange exchangeCurrent;
    private BinanceFutureStreamingExchange exchangeFutureUSDT;
    private BinanceFutureStreamingExchange exchangeFutureCoin;
    private BinanceStreamingExchange exchangeSpot;

    public ExchangeContext(int tradeType) {
        this.tradeType = ExchangeTradeType.getEnum(tradeType);
        switch (this.tradeType) {
            case SPOT:
                exchangeSpot();
                break;

            case FUTURE_USDT:
                exchangeFutureUSDT();
                break;

            case FUTURE_COIN:
                exchangeFutureCoin();
                break;

            default:
                throw new RuntimeException("exchange is null");
        }
    }

    public BinanceStreamingExchange getExchangeCurrentStream() {
        return (BinanceStreamingExchange) exchangeCurrent;
    }

    private BinanceExchangeSpecification getFutureSpec(FuturesSettleType type) {
        BinanceExchangeSpecification spec =
                new BinanceExchangeSpecification(BinanceFutureStreamingExchange.class);
        spec.setSslUri(type == FuturesSettleType.USDT ? BinanceFuturesUSDT.URL : BinanceFuturesCoin.URL);
        spec.setHost(type == FuturesSettleType.USDT ? BinanceFuturesUSDT.HOST : BinanceFuturesCoin.HOST);
        spec.setPort(80);
        spec.setExchangeName("BinanceFutureStreamingExchange");
        spec.setExchangeDescription("Binance Futures Exchange.");

        spec.setFuturesSettleType(type);

//        spec.setApiKey(apiKey);
//        spec.setSecretKey(apiSecret);

        spec.setProxyHost("192.168.1.100");
        spec.setProxyPort(1081);

        spec.setExchangeSpecificParametersItem(StreamingExchange.SOCKS_PROXY_HOST, "192.168.1.100");
        spec.setExchangeSpecificParametersItem(StreamingExchange.SOCKS_PROXY_PORT, 1080);

        spec.setShouldLoadRemoteMetaData(false);

        return spec;
    }

    public BinanceFutureStreamingExchange exchangeFutureUSDT() {
        if (exchangeFutureUSDT != null) {
            return exchangeFutureUSDT;
        }

        BinanceExchangeSpecification spec = getFutureSpec(FuturesSettleType.USDT);
        BinanceFutureStreamingExchange exchange =
                (BinanceFutureStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);

        exchangeCurrent = exchange;
        exchangeFutureUSDT = exchange;

        return exchange;
    }

    public BinanceFutureStreamingExchange exchangeFutureCoin() {
        if (exchangeFutureCoin != null) {
            return exchangeFutureCoin;
        }
        BinanceExchangeSpecification spec = getFutureSpec(FuturesSettleType.USDT);
        BinanceFutureStreamingExchange exchange =
                (BinanceFutureStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);

        exchangeCurrent = exchange;
        exchangeFutureCoin = exchange;

        return exchange;
    }

    public BinanceStreamingExchange exchangeSpot() {
        if (exchangeSpot != null) {
            return exchangeSpot;
        }
        ExchangeSpecification spec = new ExchangeSpecification(BinanceStreamingExchange.class);
        spec.setSslUri("https://api.binance.com");
        spec.setHost("www.binance.com");
        spec.setPort(80);
        spec.setExchangeName("Binance");
        spec.setExchangeDescription("Binance Exchange.");

//        spec.setApiKey(apiKey);
//        spec.setSecretKey(apiSecret);

        spec.setProxyHost("192.168.1.100");
        spec.setProxyPort(1081);

        spec.setExchangeSpecificParametersItem(StreamingExchange.SOCKS_PROXY_HOST, "192.168.1.100");
        spec.setExchangeSpecificParametersItem(StreamingExchange.SOCKS_PROXY_PORT, 1080);

        //    spec.setShouldLoadRemoteMetaData(false);

        BinanceStreamingExchange exchange =
                (BinanceStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);

        exchangeCurrent = exchange;
        exchangeSpot = exchange;

        return exchange;
    }

}
