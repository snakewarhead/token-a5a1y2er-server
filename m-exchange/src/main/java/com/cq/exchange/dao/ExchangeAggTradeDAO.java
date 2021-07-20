package com.cq.exchange.dao;

import com.cq.exchange.entity.ExchangeAggTrade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lin on 2020-11-10.
 */
@Repository
public interface ExchangeAggTradeDAO extends MongoRepository<ExchangeAggTrade, String> {

    List<ExchangeAggTrade> findByExchangeIdAndTradeTypeAndSymbolAndTimeBetween(Integer exchangeId, Integer tradeType, String symbol, Long timeStart, Long timeEnd);

}
