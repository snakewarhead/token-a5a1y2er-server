package com.cq.exchange.task;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.cq.exchange.ExchangeContext;
import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.service.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceFuturesExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.service.BinanceFuturesMarketDataServiceRaw;

import java.util.List;

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
            return StrUtil.format("5 0/{} * * * ?", periodEnum.getNum());
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


                    if (first) {
                        ThreadUtil.sleep(INTERVAL_IN_MAX);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            // save new ones

            first = false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
