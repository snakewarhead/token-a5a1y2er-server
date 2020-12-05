package com.cq.exchange.task;

import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeTakerLongShortRatio;
import com.cq.exchange.enums.ExchangeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceFuturesExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceTakerLongShortRatio;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.service.BinanceFuturesMarketDataServiceRaw;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
public class TakerLongShortRatioGrabber implements Runnable {

    private final ExchangeContext exchangeContext;
    private final List<String> symbols;

    private String periodStr;
    private KlineInterval period;

    @Override
    public void run() {
        BinanceFuturesExchange exchange = (BinanceFuturesExchange) exchangeContext.getStreamingExchangeCurrent();
        BinanceFuturesMarketDataServiceRaw service = (BinanceFuturesMarketDataServiceRaw) exchange.getMarketDataService();

        symbols.forEach(s -> {
            try {
                List<BinanceTakerLongShortRatio> ls = service.takerlongshortRatio(s, period, 100, null, null);
                List<ExchangeTakerLongShortRatio> lsAdapt = ls.stream().map(this::adapt).collect(Collectors.toList());

                a ThreadPoolTaskScheduler wrapper can maintain a pool in which store taskes that need run.
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    public String cron(String periodStr) {
        this.periodStr = periodStr;
        this.period = KlineInterval.getEnum(periodStr);

        // "5m","15m","30m","1h","2h","4h","6h","12h","1d"
        // need fast 5 times
        // TODO: If No new data that grabbing in current period, that need grabbing again for a while.
        if ("5m".equals(periodStr)) {
            return "3 */1 * * * ?";
        } else if ("15m".equals(periodStr)) {
            return "3 */3 * * * ?";
        }

        throw new RuntimeException("This period is not supported. " + periodStr);
    }

    public ExchangeTakerLongShortRatio adapt(BinanceTakerLongShortRatio r) {
        return ExchangeTakerLongShortRatio.builder()
                .exchangeId(ExchangeEnum.BINANCE.getCode())
                slfj
                .build();
    }
}
