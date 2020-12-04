package com.cq.exchange.service;

import com.cq.exchange.entity.ExchangeFutureFundingRate;
import com.cq.exchange.dao.ExchangeFutureFundingRateDAO;
import com.cq.exchange.dao.ExchangeFutureFundingRateDAODynamic;
import com.mongodb.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

/**
 * Created by lin on 2020-10-29.
 */
@Service
@RequiredArgsConstructor
public class ExchangeFutureFundingRateSerivce {

    private final ExchangeFutureFundingRateDAO exchangeFutureFundingRateDAO;
    private final ExchangeFutureFundingRateDAODynamic exchangeFutureFundingRateDAODynamic;

    public ExchangeFutureFundingRate find(Integer exchangeId, Integer futureType, Integer settleType, String symbol) {
        return exchangeFutureFundingRateDAO.find(exchangeId, futureType, settleType, symbol);
    }

    public Page<ExchangeFutureFundingRate> findByExchange(Integer exchangeId, String name, String pair, int page, int size, int direction, String fieldOfDirection) {
        Pageable pageable = PageRequest.of(page, size, direction == 0 ? Sort.Direction.ASC : Sort.Direction.DESC, fieldOfDirection);

        QueryBuilder queryBuilder = new QueryBuilder();

        if (exchangeId != null) {
            queryBuilder.and("exchangeId").is(exchangeId);
        }
        if (StringUtils.isNotEmpty(name)) {
            queryBuilder.and("name").is(name);
        }
        if (StringUtils.isNotEmpty(pair)) {
            queryBuilder.and("symbol").is(pair);
        }

        return exchangeFutureFundingRateDAODynamic.findByQuery(new BasicQuery(queryBuilder.get().toString()), pageable);
    }

    public void save(ExchangeFutureFundingRate r) {
        ExchangeFutureFundingRate rr = find(r.getExchangeId(), r.getFutureType(), r.getSettleType(), r.getSymbol());

        if (rr == null) {
            rr = new ExchangeFutureFundingRate();
            rr.setExchangeId(r.getExchangeId());
            rr.setFutureType(r.getFutureType());
            rr.setSettleType(r.getSettleType());
            rr.setName(r.getName());
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

}
