package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeCoinInfoDAO;
import com.cq.exchange.dao.ExchangeCoinInfoDAODynamic;
import com.cq.exchange.entity.ExchangeCoinInfo;
import com.mongodb.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Created by lin on 2020-11-10.
 */
@Service
@RequiredArgsConstructor
public class ExchangeCoinInfoService {

    private final ExchangeCoinInfoDAO exchangeCoinInfoDAO;
    private final ExchangeCoinInfoDAODynamic exchangeCoinInfoDAODynamic;

    public void save(ExchangeCoinInfo e) {
        ExchangeCoinInfo old = exchangeCoinInfoDAO.findByExchangeIdAndTradeTypeAndSymbolAndPeriod(e.getExchangeId(), e.getTradeType(), e.getSymbol(), e.getPeriod());
        if (old != null) {
            e.setId(old.getId());
        }
        exchangeCoinInfoDAO.save(e);
    }

    public ExchangeCoinInfo find(Integer exchangeId, Integer tradeType, String symbol, String period) {
        return exchangeCoinInfoDAO.findByExchangeIdAndTradeTypeAndSymbolAndPeriod(exchangeId, tradeType, symbol, period);
    }

    public static Query buildQuery(ExchangeCoinInfo e) {
        QueryBuilder qb = new QueryBuilder();
        qb.and("exchangeId").is(e.getExchangeId());
        qb.and("tradeType").is(e.getTradeType());
        qb.and("symbol").is(e.getSymbol());
        qb.and("pair").is(e.getPair());

        qb.and("period").is(e.getPeriod());

        return new BasicQuery(qb.get().toString());
    }

    public void updateOne(ExchangeCoinInfo e) {
        exchangeCoinInfoDAODynamic.upsertWrap(buildQuery(e), e);
    }
}
