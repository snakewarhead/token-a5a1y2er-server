package com.cq.exchange.task;

import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeTakerLongShortRatio;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.service.ServiceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceFuturesExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceTakerLongShortRatio;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.service.BinanceFuturesMarketDataServiceRaw;
import org.knowm.xchange.instrument.Instrument;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
public class TakerLongShortRatioGrabber implements Grabber {

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;

    private final String symbol;
    private final String period;
    private final KlineInterval periodEnum;

    private BinanceFuturesMarketDataServiceRaw binanceFuturesMarketDataServiceRaw;

    public TakerLongShortRatioGrabber(ServiceContext serviceContext, ExchangeContext exchangeContext, String symbol, String period) {
        this.serviceContext = serviceContext;
        this.exchangeContext = exchangeContext;
        this.symbol = symbol;
        this.period = period;
        this.periodEnum = KlineInterval.getEnum(period);

        BinanceFuturesExchange exchange = (BinanceFuturesExchange) exchangeContext.getExchangeCurrent();
        binanceFuturesMarketDataServiceRaw = (BinanceFuturesMarketDataServiceRaw) exchange.getMarketDataService();
    }

    public static String cron(String periodStr) {
        // "5m","15m","30m","1h","2h","4h","6h","12h","1d"
        // need fast 5 multiple
        // TODO: If No new data that grabbing in current period, that need grabbing again for a while.
        if ("5m".equals(periodStr)) {
            return "3 */1 * * * ?";
        } else {
            return "3 */5 * * * ?";
        }
    }

    private boolean first = true;

    private final static int MAX = 100;
    private final static int MIN = 5;

    @Override
    public void run() {
        try {
            int limit = first ? MAX : MIN;
            List<BinanceTakerLongShortRatio> ls = binanceFuturesMarketDataServiceRaw.takerlongshortRatio(symbol, periodEnum, limit, null, null);
            List<ExchangeTakerLongShortRatio> lsAdapt = ls.stream().map(e -> adapt(symbol, e)).collect(Collectors.toList());

            serviceContext.getExchangeTakerLongShortRatioService().saveAll(lsAdapt);

            first = false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public ExchangeTakerLongShortRatio adapt(String symbol, BinanceTakerLongShortRatio r) {
        Instrument pair = BinanceAdapters.adaptSymbol(symbol, true);
        ExchangeTakerLongShortRatio rr = new ExchangeTakerLongShortRatio();
        rr.setExchangeId(ExchangeEnum.BINANCE.getCode());
        rr.setTradeType(exchangeContext.getTradeType().getCode());
        rr.setSymbol(symbol);
        rr.setPeriod(period);
        rr.setPair(pair.toString());
        rr.setBaseSymbol(pair.getBase().getCurrencyCode());
        rr.setBuyVol(r.getBuyVol());
        rr.setSellVol(r.getSellVol());
        rr.setBuySellRatio(r.getBuySellRatio());
        rr.setTime(r.getTimestamp());
        return rr;
    }
}
