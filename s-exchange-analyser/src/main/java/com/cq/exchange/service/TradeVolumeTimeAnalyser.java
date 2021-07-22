package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.exchange.entity.ExchangeAggTrade;
import com.cq.exchange.entity.ExchangeTradeVolumeTime;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
public class TradeVolumeTimeAnalyser implements Runnable {

    private final ServiceContext serviceContext;

    private final ExchangeEnum exchangeEnum;
    private final ExchangeTradeType tradeType;
    private final String symbol;
    private final String period;
    private ExchangePeriodEnum periodEnum;

    public TradeVolumeTimeAnalyser init() {
        periodEnum = ExchangePeriodEnum.getEnum(period);

        return this;
    }

    public static String cron(String periodStr) {
        if ("5m".equals(periodStr)) {
            return "0/10 * * * * ?";
        } else if ("15m".equals(periodStr)) {
            return "3 0/1 * * * ?";
        } else if ("1h".equals(periodStr)) {
            return "3 0/5 * * * ?";
        } else if ("4h".equals(periodStr)) {
            return "3 0/10 * * * ?";
        }

        throw new RuntimeException("This period is not supported. " + periodStr);
    }

    private boolean init = true;

    @Override
    public void run() {
        if (init) {
            do {
                // 启动时，旧的时间统计数据最后到哪个时间
                ExchangeTradeVolumeTime last = serviceContext.getExchangeTradeVolumeTimeService().findLast(exchangeEnum.getCode(), tradeType.getCode(), symbol, period);
                if (last == null) {
                    break;
                }

                // 那个时间之后的数据，逐一分析
                List<ExchangeAggTrade> trades = serviceContext.getExchangeAggTradeService().find(exchangeEnum.getCode(), tradeType.getCode(), symbol, last.getTime().getTime(), null);
                if (CollUtil.isEmpty(trades)) {
                    break;
                }

                AtomicLong timeCurr = new AtomicLong(last.getTime().getTime());
                AtomicLong timeEnd = new AtomicLong(timeCurr.get() + periodEnum.getMillis());
                trades.forEach(t -> {
                    if (t.getTime() >= timeEnd.get()) {
                        新的周期了
                        timeCurr.set(timeEnd.get());
                        timeEnd.set(timeCurr.get() + periodEnum.getMillis());
                    }

                    ExchangeTradeVolumeTime v = serviceContext.getExchangeTradeVolumeTimeService().find(exchangeEnum.getCode(), tradeType.getCode(), symbol, period, new Date(timeCurr.get()));
                    if (v == null) {
                        v = new ExchangeTradeVolumeTime();
                        v.setExchangeId(exchangeEnum.getCode());
                        v.setTradeType(tradeType.getCode());
                        v.setSymbol(symbol);
                        v.setPeriod(period);
                        v.setTime(new Date(timeCurr.get()));
                    }
                    v.reset();
                });

            } while (true);

            init = false;
        }

        // 获取当前时间段的数据

        // 分析

        // 保存这个时间段的统计数据
    }

}
