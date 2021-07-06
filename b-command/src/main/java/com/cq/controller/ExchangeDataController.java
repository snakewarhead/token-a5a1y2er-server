package com.cq.controller;

import com.cq.exchange.entity.ExchangeOrderBook;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import com.cq.exchange.service.ExchangeOrderBookService;
import com.cq.vo.JSONResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/exchange/data")
public class ExchangeDataController {

    private final ExchangeOrderBookService exchangeOrderBookService;

    @GetMapping("orderBook")
    public JSONResult<ExchangeOrderBook> orderBook(int exchange, int tradeType, String symbol) {
        if (!ExchangeEnum.contains(exchange)) {
            return JSONResult.error(400, "params error 1");
        }
        if (!ExchangeTradeType.contains(tradeType)) {
            return JSONResult.error(400, "params error 2");
        }

        ExchangeOrderBook result = exchangeOrderBookService.find(exchange, tradeType, symbol);

        return JSONResult.success("", result);
    }

}
