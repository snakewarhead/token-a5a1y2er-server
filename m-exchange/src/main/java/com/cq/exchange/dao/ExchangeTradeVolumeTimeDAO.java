package com.cq.exchange.dao;

import com.cq.exchange.entity.ExchangeTradeVolumeTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by lin on 2020-11-10.
 */
@Repository
public interface ExchangeTradeVolumeTimeDAO extends MongoRepository<ExchangeTradeVolumeTime, String> {

    ExchangeTradeVolumeTime findByExchangeIdAndTradeTypeAndSymbolAndPeriodAndTime(Integer exchangeId, Integer tradeType, String symbol, String period, Date time);

}
