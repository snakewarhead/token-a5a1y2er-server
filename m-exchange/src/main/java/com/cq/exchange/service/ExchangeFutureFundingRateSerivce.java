package com.cq.exchange.service;

import cn.hutool.core.util.StrUtil;
import com.cq.exchange.dao.ExchangeFutureFundingRateDAO;
import com.cq.exchange.dao.ExchangeFutureFundingRateDAODynamic;
import com.cq.exchange.entity.ExchangeFutureFundingRate;
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
 * Created by lin on 2020-10-29.
 */
@Service
@RequiredArgsConstructor
public class ExchangeFutureFundingRateSerivce {

    private final ExchangeFutureFundingRateDAO exchangeFutureFundingRateDAO;
    private final ExchangeFutureFundingRateDAODynamic exchangeFutureFundingRateDAODynamic;

    public ExchangeFutureFundingRate find(Integer exchangeId, Integer tradeType, String symbol) {
        return exchangeFutureFundingRateDAO.findByExchangeIdAndTradeTypeAndSymbol(exchangeId, tradeType, symbol);
    }

    public Page<ExchangeFutureFundingRate> findByExchange(Integer exchangeId, String name, String pair, int page, int size, int direction, String fieldOfDirection) {
        Pageable pageable = PageRequest.of(page, size, direction == 0 ? Sort.Direction.ASC : Sort.Direction.DESC, fieldOfDirection);

        QueryBuilder queryBuilder = new QueryBuilder();

        if (exchangeId != null) {
            queryBuilder.and("exchangeId").is(exchangeId);
        }
        if (StrUtil.isNotEmpty(name)) {
            queryBuilder.and("name").is(name);
        }
        if (StrUtil.isNotEmpty(pair)) {
            queryBuilder.and("symbol").is(pair);
        }

        return exchangeFutureFundingRateDAODynamic.findByQuery(new BasicQuery(queryBuilder.get().toString()), pageable);
    }

    public void save(ExchangeFutureFundingRate r) {
        ExchangeFutureFundingRate rr = find(r.getExchangeId(), r.getTradeType(), r.getSymbol());

        if (rr == null) {
            rr = new ExchangeFutureFundingRate();
            rr.setExchangeId(r.getExchangeId());
            rr.setTradeType(r.getTradeType());
            rr.setSymbol(r.getSymbol());
            rr.setPair(r.getPair());
        }
        rr.setMarkPrice(r.getMarkPrice());
        rr.setIndexPrice(r.getIndexPrice());
        rr.setLastFundingRate(r.getLastFundingRate());
        rr.setNextFundingTime(r.getNextFundingTime());
        rr.setTime(r.getTime());
        rr.setEstimatedRate(r.getEstimatedRate());

        exchangeFutureFundingRateDAO.save(rr);
    }

    public void saveAll(List<ExchangeFutureFundingRate> ls) {
        exchangeFutureFundingRateDAO.saveAll(ls);
    }

}
