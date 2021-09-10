package com.cq.exchange.dao;

import com.cq.exchange.entity.ExchangeCoinInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lin on 2020-11-10.
 */
@Repository
public interface ExchangeCoinInfoDAO extends MongoRepository<ExchangeCoinInfo, String> {

    ExchangeCoinInfo findByExchangeIdAndTradeTypeAndSymbolAndPeriod(Integer exchangeId, Integer tradeType, String symbol,String period);
}
