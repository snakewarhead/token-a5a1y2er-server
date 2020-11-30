package com.cq.service;

import com.cq.dao.ExchangeOrderBookDAO;
import com.cq.entity.ExchangeOrderBook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Created by lin on 2020-11-10.
 */
@Service
@RequiredArgsConstructor
public class ExchangeOrderBookService {

    private final ExchangeOrderBookDAO exchangeOrderBookDAO;

    public void save(ExchangeOrderBook orderBook) {
        ExchangeOrderBook exist = exchangeOrderBookDAO.find(orderBook.getExchangeId(), orderBook.getTradeType(), orderBook.getSymbol());
        if (exist != null) {
            orderBook.setId(exist.getId());
        }
        exchangeOrderBookDAO.save(orderBook);
    }

}
