package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeTickerDAO;
import com.cq.exchange.dao.ExchangeTickerDAODynamic;
import com.cq.exchange.entity.ExchangeTicker;
import com.mongodb.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ExchangeTickerService {

    private final ExchangeTickerDAO exchangeTickerDAO;
    private final ExchangeTickerDAODynamic exchangeTickerDAODynamic;

    public void saveAll(List<ExchangeTicker> ls) {
        List<Pair<Query, ExchangeTicker>> updates = ls.stream().map(i -> {
            QueryBuilder qb = new QueryBuilder();
            qb.and("exchangeId").is(i.getExchangeId());
            qb.and("tradeType").is(i.getTradeType());
            qb.and("symbol").is(i.getSymbol());
            Query q = new BasicQuery(qb.toString());
            return Pair.of(q, i);
        }).collect(Collectors.toList());
        exchangeTickerDAODynamic.bulkUpsertWrap(false, updates);
    }
}
