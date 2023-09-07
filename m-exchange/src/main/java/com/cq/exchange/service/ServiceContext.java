package com.cq.exchange.service;

import com.cq.core.service.HtmlContentBuilder;
import com.cq.core.service.MailClient;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ExchangeCoinInfoService exchangeCoinInfoService;
    private final ExchangeTradeVolumeTimeService exchangeTradeVolumeTimeService;
    private final ExchangeFutureFundingRateSerivce exchangeFutureFundingRateSerivce;

    private final MailClient mailClient;
    private final HtmlContentBuilder htmlContentBuilder;
    private final ObjectMapper jsonMapper = new ObjectMapper();
}
