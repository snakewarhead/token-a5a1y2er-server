package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeAggTradeDAO;
import com.cq.exchange.entity.ExchangeAggTrade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Created by lin on 2020-11-10.
 */
@Service
@RequiredArgsConstructor
public class ExchangeAggTradeService {

    private final ExchangeAggTradeDAO exchangeAggTradeDAO;

    public void save(ExchangeAggTrade e) {
        exchangeAggTradeDAO.save(e);
    }

}
