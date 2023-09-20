package com.cq.exchange.dao;

import com.cq.exchange.entity.ExchangeKline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lin on 2020-11-10.
 */
@Repository
public interface ExchangeKlineDAO extends MongoRepository<ExchangeKline, String> {

    ExchangeKline findFirstByExchangeIdAndTradeTypeAndSymbolAndPeriodOrderByOpenTimeDesc(Integer exchangeId, Integer tradeType, String symbol, String period);

    Page<ExchangeKline> findByExchangeIdAndTradeTypeAndSymbolAndPeriod(Integer exchangeId, Integer tradeType, String symbol, String period, Pageable pageable);

    @Aggregation(value = {
            "{ '$match': { 'exchangeId': ?0, 'tradeType': ?1, 'symbol': ?2, 'period': ?3 } }",
            "{ '$sort': { ?4: ?5 }",
            "{ '$skip': ?6 }",
            "{ '$limit': ?7 }",
    })
    List<ExchangeKline> findMore(Integer exchangeId, Integer tradeType, String symbol, String period, String orderField, int orderDirection, int skip, int limit);
}
