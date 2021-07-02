package com.cq.exchange;

import com.cq.exchange.enums.ExchangeTradeType;
import com.cq.exchange.service.ExchangeAggTradeService;
import com.cq.exchange.service.ExchangeForceOrderService;
import com.cq.exchange.service.ExchangeOrderBookService;
import com.cq.exchange.service.ExchangeTakerLongShortRatioService;
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
@RequiredArgsConstructor
@Getter
@Component
public class ExchangeContext {

    private final ExchangeForceOrderService exchangeForceOrderService;
    private final ExchangeAggTradeService exchangeAggTradeService;
    private final ExchangeOrderBookService exchangeOrderBookService;
    private final ExchangeTakerLongShortRatioService exchangeTakerLongShortRatioService;

    private ExchangeTradeType tradeType;
    private BaseExchange exchangeCurrent;

    private BinanceFutureStreamingExchange exchangeFutureUSDT;
    private BinanceFutureStreamingExchange exchangeFutureCoin;
    private BinanceStreamingExchange exchangeSpot;

    public void initExchange(int type) {
        if (ExchangeTradeType.SPOT.is(type)) {
            exchangeSpot();
        } else if (ExchangeTradeType.FUTURE_USDT.is(type)) {
            exchangeFutureUSDT();
        } else if (ExchangeTradeType.FUTURE_COIN.is(type)) {
            exchangeFutureCoin();
        } else {
            throw new RuntimeException("exchange is null");
        }
    }

    public BinanceFutureStreamingExchange getExchangeCurrentStream() {
        return (BinanceFutureStreamingExchange) exchangeCurrent;
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

        tradeType = ExchangeTradeType.FUTURE_USDT;
        exchangeCurrent = exchange;

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
        exchangeCurrent = exchange;

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
        exchangeCurrent = exchange;

        return exchange;
    }

}
