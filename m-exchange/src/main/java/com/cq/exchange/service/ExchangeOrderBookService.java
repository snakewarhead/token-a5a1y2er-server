package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeOrderBookDAO;
import com.cq.exchange.entity.ExchangeOrderBook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Created by lin on 2020-11-10.
 */
@Service
@RequiredArgsConstructor
public class ExchangeOrderBookService {

    private final ExchangeOrderBookDAO exchangeOrderBookDAO;

    public ExchangeOrderBook find(Integer exchangeId, Integer tradeType, String symbol) {
        return exchangeOrderBookDAO.findByExchangeIdAndTradeTypeAndSymbol(exchangeId, tradeType, symbol);
    }

    public void save(ExchangeOrderBook orderBook) {
        ExchangeOrderBook exist = exchangeOrderBookDAO.findByExchangeIdAndTradeTypeAndSymbol(orderBook.getExchangeId(), orderBook.getTradeType(), orderBook.getSymbol());
        if (exist != null) {
            orderBook.setId(exist.getId());
        }
        exchangeOrderBookDAO.save(orderBook);
    }

}
