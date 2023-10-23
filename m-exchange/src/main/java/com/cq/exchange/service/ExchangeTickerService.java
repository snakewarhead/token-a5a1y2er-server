package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeTickerDAO;
import com.cq.exchange.dao.ExchangeTickerDAODynamic;
import com.cq.exchange.entity.ExchangeTicker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ExchangeTickerService extends ExchangeBaseService<ExchangeTicker> {

    private final ExchangeTickerDAO exchangeTickerDAO;
    private final ExchangeTickerDAODynamic exchangeTickerDAODynamic;

    public ExchangeTicker find(Integer exchangeId, Integer tradeType, String symbol) {
        return exchangeTickerDAO.findByExchangeIdAndTradeTypeAndSymbol(exchangeId, tradeType, symbol);
    }

    public void saveAll(List<ExchangeTicker> ls) {
        saveAll(exchangeTickerDAODynamic, ls);
    }
}
