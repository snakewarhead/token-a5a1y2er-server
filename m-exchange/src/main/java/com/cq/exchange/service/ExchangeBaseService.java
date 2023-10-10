package com.cq.exchange.service;

import com.cq.core.dao.BaseDAODynamic;
import com.cq.exchange.entity.ExchangeEntity;
import com.mongodb.QueryBuilder;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeBaseService<T extends ExchangeEntity> {

    protected void saveAll(BaseDAODynamic<T> dao, List<T> ls) {
        List<Pair<Query, T>> updates = ls.stream().map(i -> {
            QueryBuilder qb = new QueryBuilder();
            qb.and("exchangeId").is(i.getExchangeId());
            qb.and("tradeType").is(i.getTradeType());
            qb.and("symbol").is(i.getSymbol());
            Query q = new BasicQuery(qb.get().toString());
            return Pair.of(q, i);
        }).collect(Collectors.toList());
        dao.bulkUpsertWrap(false, updates);
    }
}
