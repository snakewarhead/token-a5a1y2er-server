package com.cq.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FundingRateRankAnalyser implements Runnable {

    private final ServiceContext serviceContext;

    public FundingRateRankAnalyser init() {

        return this;
    }

    @Override
    public void run() {
    }
}
