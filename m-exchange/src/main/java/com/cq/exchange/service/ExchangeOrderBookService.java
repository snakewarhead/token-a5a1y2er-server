package com.cq.exchange.service;

import cn.hutool.core.util.StrUtil;
import com.cq.exchange.dao.ExchangeOrderBookDAO;
import com.cq.exchange.dao.ExchangeOrderBookDAODynamic;
import com.cq.exchange.entity.ExchangeOrderBook;
import com.mongodb.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lin on 2020-11-10.
 */
@Service
@RequiredArgsConstructor
public class ExchangeOrderBookService {

    private final ExchangeOrderBookDAO exchangeOrderBookDAO;
    private final ExchangeOrderBookDAODynamic exchangeOrderBookDAODynamic;

    public ExchangeOrderBook find(Integer exchangeId, Integer tradeType, String symbol) {
        return exchangeOrderBookDAO.findByExchangeIdAndTradeTypeAndSymbol(exchangeId, tradeType, symbol);
    }

    public void save(ExchangeOrderBook orderBook) {
        ExchangeOrderBook exist = exchangeOrderBookDAO.findByExchangeIdAndTradeTypeAndSymbol(orderBook.getExchangeId(), orderBook.getTradeType(), orderBook.getSymbol());
        if (exist != null) {
            orderBook.setId(exist.getId());
        }
        exchangeOrderBookDAO.save(orderBook);
    }

    public static Query buildQuery(ExchangeOrderBook e) {
        QueryBuilder qb = new QueryBuilder();
        qb.and("exchangeId").is(e.getExchangeId());
        qb.and("tradeType").is(e.getTradeType());
        qb.and("symbol").is(e.getSymbol());

        return new BasicQuery(qb.get().toString());
    }

    public void updateDiff(ExchangeOrderBook orderBook) {
        Query q = buildQuery(orderBook);

        // update
        Document d = new Document();

        List<ExchangeOrderBook.Order> asks = orderBook.getAsks();
        for (int i = 0; i < asks.size(); ++i) {
            ExchangeOrderBook.Order o = asks.get(i);
            d.append(StrUtil.format("asks.$[eleA{}].amount", i), o.getAmount());
        }
        List<ExchangeOrderBook.Order> bids = orderBook.getBids();
        for (int i = 0; i < bids.size(); ++i) {
            ExchangeOrderBook.Order o = bids.get(i);
            d.append(StrUtil.format("bids.$[eleB{}].amount", i), o.getAmount());
        }

        Document docu = new Document().append("$set", d);
        Update u = Update.fromDocument(docu);

        // arrayFilters
        for (int i = 0; i < asks.size(); ++i) {
            ExchangeOrderBook.Order o = asks.get(i);
            u.filterArray(StrUtil.format("eleA{}.price", i), o.getPrice());
        }
        for (int i = 0; i < bids.size(); ++i) {
            ExchangeOrderBook.Order o = bids.get(i);
            u.filterArray(StrUtil.format("eleB{}.price", i), o.getPrice());
        }

        exchangeOrderBookDAODynamic.updateFirst(q, u);
    }
}
