package com.cq.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
public class TradeVolumeTimeAnalyser implements Runnable {

    private final ServiceContext serviceContext;

    private final String symbol;
    private final String period;

    public TradeVolumeTimeAnalyser init() {
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
            // 启动时，久的时间统计数据最后到哪个时间
            // 那个时间之后的数据，逐一分析

            init = false;
        }

        // 获取当前时间段的数据

        // 分析

        // 保存这个时间段的统计数据
    }

}
