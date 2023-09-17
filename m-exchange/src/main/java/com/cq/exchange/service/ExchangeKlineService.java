package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeKlineDAO;
import com.cq.exchange.dao.ExchangeKlineDAODynamic;
import com.cq.exchange.entity.ExchangeKline;
import com.mongodb.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ExchangeKlineService extends ExchangeBaseService<ExchangeKline> {

    private final ExchangeKlineDAO exchangeKlineDAO;
    private final ExchangeKlineDAODynamic exchangeKlineDAODynamic;

    public void saveAll(List<ExchangeKline> ls) {
        List<Pair<Query, ExchangeKline>> updates = ls.stream().map(i -> {
            QueryBuilder qb = new QueryBuilder();
            qb.and("exchangeId").is(i.getExchangeId());
            qb.and("tradeType").is(i.getTradeType());
            qb.and("symbol").is(i.getSymbol());
            qb.and("pair").is(i.getPair());
            qb.and("openTime").is(i.getOpenTime());
            Query q = new BasicQuery(qb.get().toString());
            return Pair.of(q, i);
        }).collect(Collectors.toList());
        exchangeKlineDAODynamic.bulkUpsertWrap(false, updates);
    }

}
