package com.cq.exchange.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Getter
@Service
public class ServiceContext {

    private final ExchangeForceOrderService exchangeForceOrderService;
    private final ExchangeAggTradeService exchangeAggTradeService;
    private final ExchangeOrderBookService exchangeOrderBookService;
    private final ExchangeTakerLongShortRatioService exchangeTakerLongShortRatioService;

    private final ExchangeTradeVolumeTimeService exchangeTradeVolumeTimeService;
}
