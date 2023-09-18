package com.cq.exchange.dao;

import com.cq.exchange.entity.ExchangeKline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lin on 2020-11-10.
 */
@Repository
public interface ExchangeKlineDAO extends MongoRepository<ExchangeKline, String> {

    ExchangeKline findFirstByExchangeIdAndTradeTypeAndSymbolAndPeriodOrderByOpenTimeDesc(Integer exchangeId, Integer tradeType, String symbol, String period);

    Page<ExchangeKline> findByExchangeIdAndTradeTypeAndSymbolAndPeriodOrder(Integer exchangeId, Integer tradeType, String symbol, String period, Pageable pageable);
}
