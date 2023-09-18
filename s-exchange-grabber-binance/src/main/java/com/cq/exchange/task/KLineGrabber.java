package com.cq.exchange.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.entity.ExchangeKline;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.service.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceFuturesExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.service.BinanceFuturesMarketDataServiceRaw;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
public class KLineGrabber implements Runnable {

    private final static int INTERVAL_IN_MAX = 500; // 2400 weights per minute
    private final static int MAX = 1000;    // weight - 5
    private final static int MIN = 99;      // weight - 1

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;
    private final ExchangePeriodEnum periodEnum;

    private BinanceFuturesMarketDataServiceRaw binanceFuturesMarketDataServiceRaw;
    private boolean first = true;

    public KLineGrabber(ServiceContext serviceContext, ExchangeContext exchangeContext, String period) {
        this.serviceContext = serviceContext;
        this.exchangeContext = exchangeContext;
        this.periodEnum = ExchangePeriodEnum.getEnum(period);

        BinanceFuturesExchange exchange = (BinanceFuturesExchange) exchangeContext.getExchangeCurrent();
        binanceFuturesMarketDataServiceRaw = (BinanceFuturesMarketDataServiceRaw) exchange.getMarketDataService();
    }

    public String cron() {
        if ("m".equals(periodEnum.getUnit())) {
            return StrUtil.format("2 0/{} * * * ?", periodEnum.getNum());
        }
        return null;
    }

    @Override
    public void run() {
        try {
            int limit = first ? MAX : MIN;

            // get all pair info
            List<ExchangeCoinInfoRaw> ls = serviceContext.getExchangeCoinInfoRawService().find(exchangeContext.getExchangeEnum().getCode(), exchangeContext.getTradeType().getCode(), 1);

            // get each kline with sleeping a while
            for (ExchangeCoinInfoRaw i : ls) {
                try {
                    List<BinanceKline> klines = binanceFuturesMarketDataServiceRaw.klines(i.getSymbol(), KlineInterval.getEnum(periodEnum.getSymbol()), limit, null, null);
                    List<ExchangeKline> klinesAdapt = klines.stream().map(this::adapt).collect(Collectors.toList());
                    if (CollUtil.isEmpty(klinesAdapt)) {
                        continue;
                    }
                    serviceContext.getExchangeKlineService().saveAll(klinesAdapt);

                    if (first) {
                        ThreadUtil.sleep(INTERVAL_IN_MAX);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            first = false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private ExchangeKline adapt(BinanceKline i) {
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
        l.setExchangeId(exchangeContext.getExchangeEnum().getCode());
        l.setTradeType(exchangeContext.getTradeType().getCode());
        l.setSymbol(symbol);
        l.setPair(pair);
        return l;
    }
}
