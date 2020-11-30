package com.cq.blockchain.task;

import com.cq.blockchain.service.EthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by lin on 2020-09-28.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ActionTask {

    private final EthService ethService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void actTest() {
        ethService.analyzeTransactionFeeRank("test");
    }

    @Scheduled(cron = "0 0 0/4 * * ?")
    public void act4hour() {
        ethService.analyzeTransactionFeeRank("4hour");
    }

    @Scheduled(cron = "0 5 0 * * ?")
    public void act1day() {
        ethService.analyzeTransactionFeeRank("1day");
    }

    @Scheduled(cron = "0 10 0 */3 * ?")
    public void act3day() {

    }

    @Scheduled(cron = "0 20 0 */10 * ?")
    public void act10day() {

    }

    @Scheduled(cron = "0 30 0 1 * ?")
    public void act1month() {

    }
}
