package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeTakerLongShortRatioDAODynamic;
import com.cq.exchange.entity.ExchangeTakerLongShortRatio;
import com.mongodb.QueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lin on 2020-12-05.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeTakerLongShortRatioService {

    private final ExchangeTakerLongShortRatioDAODynamic exchangeTakerLongShortRatioDAODynamic;

    public Page<ExchangeTakerLongShortRatio> find(Integer exchangeId, Integer tradeType, String symbol, int page, int size, int direction, String fieldOfDirection) {
        QueryBuilder queryBuilder = new QueryBuilder();

        if (exchangeId != null) {
            queryBuilder.and("exchangeId").is(exchangeId);
        }
        if (tradeType != null) {
            queryBuilder.and("tradeType").is(tradeType);
        }
        if (StringUtils.isNotEmpty(symbol)) {
            queryBuilder.and("symbol").is(symbol);
        }

        Pageable pageable = PageRequest.of(page, size, direction == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, fieldOfDirection);

        return exchangeTakerLongShortRatioDAODynamic.findByQuery(new BasicQuery(queryBuilder.get().toString()), pageable);
    }
}
