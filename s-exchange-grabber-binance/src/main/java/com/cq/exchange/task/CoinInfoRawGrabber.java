package com.cq.exchange.task;

import com.cq.exchange.ExchangeContext;
import com.cq.exchange.service.ServiceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceFuturesExchange;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.BinanceExchangeInfo;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.Filter;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.Symbol;
import org.knowm.xchange.binance.service.BinanceFuturesMarketDataService;
import org.knowm.xchange.binance.service.BinanceFuturesMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.InstrumentMetaData;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
public class CoinInfoRawGrabber implements Runnable {

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;

    private BinanceFuturesMarketDataService marketDataService;

    public static String cron() {
        return "10 1 0 * * ?";
    }

    public CoinInfoRawGrabber init() {
        BinanceFuturesExchange exchange = (BinanceFuturesExchange) exchangeContext.getExchangeCurrent();
        marketDataService = (BinanceFuturesMarketDataService) exchange.getMarketDataService();
    }

    @Override
    public void run() {
        try {
            BinanceExchangeInfo exchangeInfo = marketDataService.getExchangeInfo();
            for (Symbol s : exchangeInfo.getSymbols()) {
                int pairPrecision = 8;
                int amountPrecision = 8;

                BigDecimal minQty = null;
                BigDecimal maxQty = null;
                BigDecimal stepSize = null;

                BigDecimal counterMinQty = null;
                BigDecimal counterMaxQty = null;

                Filter[] filters = s.getFilters();

                CurrencyPair currentCurrencyPair = new CurrencyPair(s.getBaseAsset(),
                        s.getQuoteAsset());

                for (Filter filter : filters) {
                    if (filter.getFilterType().equals("PRICE_FILTER")) {
                        pairPrecision = Math.min(pairPrecision, BinanceAdapters.numberOfDecimals(filter.getTickSize()));
                        counterMaxQty = new BigDecimal(filter.getMaxPrice()).stripTrailingZeros();
                    } else if (filter.getFilterType().equals("LOT_SIZE")) {
                        amountPrecision = Math.min(amountPrecision, BinanceAdapters.numberOfDecimals(filter.getStepSize()));
                        minQty = new BigDecimal(filter.getMinQty()).stripTrailingZeros();
                        maxQty = new BigDecimal(filter.getMaxQty()).stripTrailingZeros();
                        stepSize = new BigDecimal(filter.getStepSize()).stripTrailingZeros();
                    } else if (filter.getFilterType().equals("MIN_NOTIONAL")) {
                        counterMinQty = new BigDecimal(filter.getNotional()).stripTrailingZeros();
                    }
                }

                instruments.put(
                        currentCurrencyPair,
                        new InstrumentMetaData.Builder()
                                .tradingFee(BigDecimal.valueOf(0.1))
                                .minimumAmount(minQty)
                                .maximumAmount(maxQty)
                                .counterMinimumAmount(counterMinQty)
                                .counterMaximumAmount(counterMaxQty)
                                .volumeScale(amountPrecision)
                                .priceScale(pairPrecision)
                                .amountStepSize(stepSize)
                                .marketOrderEnabled(Arrays.asList(s.getOrderTypes()).contains("MARKET"))
                                .build());

            }


        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
