package com.cq;

import com.cq.service.ExchangeOrderBookService;
import info.bitrich.xchangestream.binance.BinanceFutureStreamingExchange;
import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Component
public class ExchangeContext {

    private final ExchangeOrderBookService exchangeOrderBookService;

    private ExchangeTradeType tradeType;
    private StreamingExchange streamingExchangeCurrent;

    private BinanceFutureStreamingExchange exchangeFutureUSDT;
    private BinanceFutureStreamingExchange exchangeFutureCoin;
    private BinanceStreamingExchange exchangeSpot;

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

        tradeType = ExchangeTradeType.FUTURE_USDT;
        streamingExchangeCurrent = exchange;

        return exchange;
    }

    public BinanceFutureStreamingExchange exchangeFutureCoin() {
        if (exchangeFutureCoin != null) {
            return exchangeFutureCoin;
        }
        BinanceExchangeSpecification spec = getFutureSpec(FuturesSettleType.USDT);
        BinanceFutureStreamingExchange exchange =
                (BinanceFutureStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);

        tradeType = ExchangeTradeType.FUTURE_COIN;
        streamingExchangeCurrent = exchange;

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

        tradeType = ExchangeTradeType.SPOT;
        streamingExchangeCurrent = exchange;

        return exchange;
    }

    public ExchangeOrderBookService getExchangeOrderBookService() {
        return exchangeOrderBookService;
    }

    public ExchangeTradeType getTradeType() {
        return tradeType;
    }

    public StreamingExchange getStreamingExchangeCurrent() {
        return streamingExchangeCurrent;
    }
}
