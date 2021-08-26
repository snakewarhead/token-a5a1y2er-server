package com.cq.exchange.service;

import cn.hutool.core.util.StrUtil;
import com.cq.exchange.dao.ExchangeAggTradeDAO;
import com.cq.exchange.dao.ExchangeAggTradeDAODynamic;
import com.cq.exchange.entity.ExchangeAggTrade;
import com.mongodb.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lin on 2020-11-10.
 */
@Service
@RequiredArgsConstructor
public class ExchangeAggTradeService {

    private final ExchangeAggTradeDAO exchangeAggTradeDAO;
    private final ExchangeAggTradeDAODynamic exchangeAggTradeDAODynamic;

    public void save(ExchangeAggTrade e) {
        exchangeAggTradeDAO.save(e);
    }

    public List<ExchangeAggTrade> find(Integer exchangeId, Integer tradeType, String symbol, Long timeStart, Long timeEnd) {
        return exchangeAggTradeDAO.findByExchangeIdAndTradeTypeAndSymbolAndTimeGreaterThanEqualAndTimeLessThan(exchangeId, tradeType, symbol, timeStart, timeEnd);
    }

    public List<ExchangeAggTrade> findRecently(Integer exchangeId, Integer tradeType, String symbol, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.Direction.DESC, "_id");

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.and("exchangeId").is(exchangeId);
        queryBuilder.and("tradeType").is(tradeType);
        queryBuilder.and("symbol").is(symbol);

        Page<ExchangeAggTrade> p = exchangeAggTradeDAODynamic.findByQuery(new BasicQuery(queryBuilder.toString()), pageable);
        if (p == null) {
            return null;
        }
        return p.getContent();
    }
}
