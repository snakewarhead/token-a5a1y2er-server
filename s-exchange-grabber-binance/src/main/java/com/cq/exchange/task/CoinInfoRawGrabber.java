package com.cq.exchange.task;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.service.ServiceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceFuturesExchange;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.BinanceExchangeInfo;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.Filter;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.Symbol;
import org.knowm.xchange.binance.service.BinanceFuturesMarketDataService;
import org.knowm.xchange.currency.CurrencyPair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
public class CoinInfoRawGrabber implements Runnable {

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;

    private BinanceFuturesMarketDataService marketDataService;

    public String cron() {
        return "40 1 0 * * ?";
    }

    public CoinInfoRawGrabber init() {
        BinanceFuturesExchange exchange = (BinanceFuturesExchange) exchangeContext.getExchangeCurrent();
        marketDataService = (BinanceFuturesMarketDataService) exchange.getMarketDataService();
        return this;
    }

    @Override
    public void run() {
        StopWatch sw = new StopWatch();
        sw.start(StrUtil.format("{}", this.getClass().getName()));

        try {
            List<ExchangeCoinInfoRaw> ls = new ArrayList<>();
            BinanceExchangeInfo exchangeInfo = marketDataService.getExchangeInfo();
            for (Symbol s : exchangeInfo.getSymbols()) {
                String symbol = s.getSymbol();
                String pair = new CurrencyPair(s.getBaseAsset(), s.getQuoteAsset()).toString();

                int status = s.getStatus().equals("TRADING") ? 1 : 0;

                int pricePrecision = 8;
                BigDecimal priceMax = null;

                int quantityPrecision = 8;
                BigDecimal quantityMin = null;
                BigDecimal quantityMax = null;
                BigDecimal quantityStep = null;

                BigDecimal amountMin = null;

                for (Filter filter : s.getFilters()) {
                    if (filter.getFilterType().equals("PRICE_FILTER")) {
                        pricePrecision = Math.min(pricePrecision, BinanceAdapters.numberOfDecimals(filter.getTickSize()));
                        priceMax = new BigDecimal(filter.getMaxPrice()).stripTrailingZeros();
                    } else if (filter.getFilterType().equals("LOT_SIZE")) {
                        quantityPrecision = Math.min(quantityPrecision, BinanceAdapters.numberOfDecimals(filter.getStepSize()));
                        quantityMin = new BigDecimal(filter.getMinQty()).stripTrailingZeros();
                        quantityMax = new BigDecimal(filter.getMaxQty()).stripTrailingZeros();
                        quantityStep = new BigDecimal(filter.getStepSize()).stripTrailingZeros();
                    } else if (filter.getFilterType().equals("MIN_NOTIONAL")) {
                        amountMin = new BigDecimal(filter.getNotional()).stripTrailingZeros();
                    }
                }

                ExchangeCoinInfoRaw info = ExchangeCoinInfoRaw.builder()
                        .status(status)
                        .tradingFee(BigDecimal.valueOf(0.0002))
                        .pricePrecision(pricePrecision)
                        .priceMax(priceMax)
                        .quantityPrecision(quantityPrecision)
                        .quantityMin(quantityMin)
                        .quantityMax(quantityMax)
                        .quantityStep(quantityStep)
                        .amountMin(amountMin)
                        .build();
                info.setExchangeId(exchangeContext.getExchangeEnum().getCode());
                info.setTradeType(exchangeContext.getTradeType().getCode());
                info.setSymbol(symbol);
                info.setPair(pair);

                ls.add(info);
            }
            serviceContext.getExchangeCoinInfoRawService().saveAll(ls);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        sw.stop();
        log.info(sw.prettyPrint(TimeUnit.MILLISECONDS));
    }

}
