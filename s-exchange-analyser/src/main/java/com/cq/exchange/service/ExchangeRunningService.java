package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.exchange.enums.ExchangeActionType;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import com.cq.exchange.vo.ExchangeRunningParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeRunningService {

    private final ServiceContext serviceContext;

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private ConcurrentHashMap<ExchangeRunningParam, List<Future>> mapRunning = new ConcurrentHashMap<>();

    public void init(int poolSize) {
        threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.initialize();
    }

    public void start(ExchangeRunningParam p, boolean init) {
        if (mapRunning.containsKey(p)) {
            log.warn("start - Already running - {}", p.toString());
            return;
        }

        ExchangeEnum exchangeEnum = ExchangeEnum.getEnum(p.getExchange());
        if (exchangeEnum == null) {
            log.warn("exchange is not support");
            return;
        }
        ExchangeTradeType tradeType = ExchangeTradeType.getEnum(p.getTradeType());
        if (tradeType == null) {
            log.warn("tradeType is not support");
            return;
        }

        List<Future> futures = new ArrayList<>();
        ExchangeRunningParam.Action a = p.getAction();
        if (ExchangeActionType.CoinInfoShort.is(a.getName())) {
            a.getSymbols().forEach(s -> {
                Future f = threadPoolTaskScheduler.schedule(new CoinInfoShortAnalyser(serviceContext, exchangeEnum, tradeType, s).init(), new CronTrigger(CoinInfoShortAnalyser.cron()));
                futures.add(f);
            });
        }
        if (ExchangeActionType.CoinInfoLong.is(a.getName())) {
            a.getSymbols().forEach(s -> {
                Future f = threadPoolTaskScheduler.schedule(new CoinInfoLongAnalyser(serviceContext, exchangeEnum, tradeType, s).init(), new CronTrigger(CoinInfoLongAnalyser.cron()));
                futures.add(f);
            });
        }
        if (ExchangeActionType.TradeVolumeTime.is(a.getName())) {
            a.getParams().forEach(ap -> {
                a.getSymbols().forEach(s -> {
                    Future f = threadPoolTaskScheduler.schedule(new TradeVolumeTimeAnalyser(serviceContext, exchangeEnum, tradeType, s, ap).init(), new CronTrigger(TradeVolumeTimeAnalyser.cron(ap)));
                    futures.add(f);
                });
            });
        }

        if (!init) {
            mapRunning.put(p, futures);
        }
    }

    public void stop(ExchangeRunningParam p) {
        if (!mapRunning.containsKey(p)) {
            log.warn("stop - no running - {}", p.toString());
            return;
        }

        List<Future> futures = mapRunning.remove(p);
        if (CollUtil.isEmpty(futures)) {
            return;
        }

        futures.stream().forEach(f -> {
            try {
                f.cancel(true);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}