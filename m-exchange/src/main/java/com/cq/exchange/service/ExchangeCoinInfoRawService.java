package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeCoinInfoRawDAO;
import com.cq.exchange.dao.ExchangeCoinInfoRawDAODynamic;
import com.cq.exchange.entity.ExchangeCoinInfoRaw;
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
public class ExchangeCoinInfoRawService {

    private final ExchangeCoinInfoRawDAO exchangeCoinInfoRawDAO;
    private final ExchangeCoinInfoRawDAODynamic exchangeCoinInfoRawDAODynamic;

    public void saveAll(List<ExchangeCoinInfoRaw> ls) {
        List<Pair<Query, ExchangeCoinInfoRaw>> updates = ls.stream().map(i -> {
            QueryBuilder qb = new QueryBuilder();
            qb.and("exchangeId").is(i.getExchangeId());
            qb.and("tradeType").is(i.getTradeType());
            qb.and("symbol").is(i.getSymbol());
            qb.and("pair").is(i.getPair());
            Query q = new BasicQuery(qb.get().toString());
            return Pair.of(q, i);
        }).collect(Collectors.toList());
        exchangeCoinInfoRawDAODynamic.bulkUpsertWrap(false, updates);
    }
}
