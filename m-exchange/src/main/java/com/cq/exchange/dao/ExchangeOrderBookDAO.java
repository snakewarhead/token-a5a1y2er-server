package com.cq.exchange.dao;

import com.cq.exchange.entity.ExchangeOrderBook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by lin on 2020-11-10.
 */
@Repository
public interface ExchangeOrderBookDAO extends MongoRepository<ExchangeOrderBook, String> {

    @Query("{ 'exchangeId':?0, 'tradeType':?1, 'symbol':?2 }")
    ExchangeOrderBook find(Integer exchangeId, Integer tradeType, String symbol);

}
