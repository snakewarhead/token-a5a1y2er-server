package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.exchange.entity.ExchangeAggTrade;
import com.cq.exchange.entity.ExchangeTradeVolumeTime;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.MathUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
                ExchangeTradeVolumeTime volumeLast = serviceContext.getExchangeTradeVolumeTimeService().findLast(exchangeEnum.getCode(), tradeType.getCode(), symbol, period);
                if (volumeLast == null) {
                    break;
                }
                ExchangeTradeVolumeTime volumeCurr = volumeLast;
                volumeCurr.reset();

                // 那个时间之后的数据，逐一分析
                List<ExchangeAggTrade> trades = serviceContext.getExchangeAggTradeService().find(exchangeEnum.getCode(), tradeType.getCode(), symbol, volumeLast.getTime().getTime(), null);
                if (CollUtil.isEmpty(trades)) {
                    break;
                }

                long timeCurr = volumeLast.getTime().getTime();
                long timeEnd = timeCurr + periodEnum.getMillis();
                for (ExchangeAggTrade t : trades) {
                    if (t.getTime() >= timeEnd) {
                        timeCurr = periodEnum.beginOfInterval(t.getTime()).getTime();
                        timeEnd = timeCurr + periodEnum.getMillis();

                        volumeCurr = serviceContext.getExchangeTradeVolumeTimeService().find(exchangeEnum.getCode(), tradeType.getCode(), symbol, period, new Date(timeCurr));
                        if (volumeCurr == null) {
                            volumeCurr = new ExchangeTradeVolumeTime();
                            volumeCurr.setExchangeId(exchangeEnum.getCode());
                            volumeCurr.setTradeType(tradeType.getCode());
                            volumeCurr.setSymbol(symbol);
                            volumeCurr.setPeriod(period);
                            volumeCurr.setTime(new Date(timeCurr));
                        }
                        volumeCurr.reset();
                    }

                    if (t.getBuyerMaker()) {
                        volumeCurr.setQtySellerTotall(MathUtil.add(volumeCurr.getQtySellerTotall(), BigDecimal.valueOf(t.getQuantity())));
                        // 根据历史trade获取 平滑平均交易量
                        // 小单: < 0.5stdev
                        // 中单：0.5stdev < q < 1.5stdev
                        // 大单：1.5stdev
                    } else {
                        volumeCurr.setQtyBuyerTotal(MathUtil.add(volumeCurr.getQtyBuyerTotal(), BigDecimal.valueOf(t.getQuantity())));
                    }

                    volumeCurr = serviceContext.getExchangeTradeVolumeTimeService().save(volumeCurr);
                }

            } while (true);

            init = false;
        }

        // 获取当前时间段的数据

        // 分析

        // 保存这个时间段的统计数据
    }

}
