package com.cq.exchange.task;

import cn.hutool.core.util.StrUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.service.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceFuturesExchange;
import org.knowm.xchange.binance.service.BinanceFuturesMarketDataServiceRaw;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
public class KLineGrabber implements Runnable {

    private final ServiceContext serviceContext;
    private final ExchangeContext exchangeContext;

    private final ExchangePeriodEnum periodEnum;

    private BinanceFuturesMarketDataServiceRaw binanceFuturesMarketDataServiceRaw;

    public KLineGrabber(ServiceContext serviceContext, ExchangeContext exchangeContext, String period) {
        this.serviceContext = serviceContext;
        this.exchangeContext = exchangeContext;
        this.periodEnum = ExchangePeriodEnum.getEnum(period);

        BinanceFuturesExchange exchange = (BinanceFuturesExchange) exchangeContext.getExchangeCurrent();
        binanceFuturesMarketDataServiceRaw = (BinanceFuturesMarketDataServiceRaw) exchange.getMarketDataService();
    }

    public String cron() {
        if ("m".equals(periodEnum.getUnit())) {
            return StrUtil.format("5 0/{} * * * ?", periodEnum.getNum());
        }
        return null;
    }

    private boolean first = true;

    private final static int MAX = 1000;    // weight - 5
    private final static int MIN = 99;      // weight - 1

    @Override
    public void run() {
        try {
            int limit = first ? MAX : MIN;

            // get all pair info

            // get kline

            // save new ones

            first = false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
