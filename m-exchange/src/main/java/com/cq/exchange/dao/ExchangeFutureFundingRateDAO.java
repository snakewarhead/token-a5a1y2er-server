package com.cq.exchange.dao;

import com.cq.exchange.entity.ExchangeFutureFundingRate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by lin on 2020-09-24.
 */
@Repository
public interface ExchangeFutureFundingRateDAO extends MongoRepository<ExchangeFutureFundingRate, String> {

    ExchangeFutureFundingRate findByExchangeIdAndTradeTypeAndSymbol(Integer exchangeId, Integer tradeType, String symbol);

}
