package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeAggTradeDAO;
import com.cq.exchange.dao.ExchangeAggTradeDAODynamic;
import com.cq.exchange.entity.ExchangeAggTrade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lin on 2020-11-10.
 */
@Service
@RequiredArgsConstructor
public class ExchangeAggTradeService {

    private final ExchangeAggTradeDAO exchangeAggTradeDAO;
    private final ExchangeAggTradeDAODynamic exchangeAggTradeDAODynamic;

    public void save(ExchangeAggTrade e) {
        exchangeAggTradeDAO.save(e);
    }

    public List<ExchangeAggTrade> find(Integer exchangeId, Integer tradeType, String symbol, Long timeStart, Long timeEnd) {
        return exchangeAggTradeDAO.findByExchangeIdAndTradeTypeAndSymbolAndTimeBetween(exchangeId, tradeType, symbol, timeStart, timeEnd);
    }
}
