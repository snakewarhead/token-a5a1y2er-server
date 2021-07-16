package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cq.exchange.dao.ExchangeTakerLongShortRatioDAODynamic;
import com.cq.exchange.entity.ExchangeTakerLongShortRatio;
import com.mongodb.QueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lin on 2020-12-05.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeTakerLongShortRatioService {

    private final ExchangeTakerLongShortRatioDAODynamic exchangeTakerLongShortRatioDAODynamic;

    public Page<ExchangeTakerLongShortRatio> find(Integer exchangeId, Integer tradeType, String symbol, String period, int page, int size, int direction, String fieldOfDirection) {
        QueryBuilder queryBuilder = new QueryBuilder();

        if (exchangeId != null) {
            queryBuilder.and("exchangeId").is(exchangeId);
        }
        if (tradeType != null) {
            queryBuilder.and("tradeType").is(tradeType);
        }
        if (StrUtil.isNotEmpty(symbol)) {
            queryBuilder.and("symbol").is(symbol);
        }
        if (StrUtil.isNotEmpty(period)) {
            queryBuilder.and("period").is(period);
        }

        Pageable pageable = PageRequest.of(page, size, direction == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, fieldOfDirection);

        return exchangeTakerLongShortRatioDAODynamic.findByQuery(new BasicQuery(queryBuilder.get().toString()), pageable);
    }

    public void saveAll(List<ExchangeTakerLongShortRatio> ls) {
        if (CollUtil.isEmpty(ls)) {
            return;
        }

        List<ExchangeTakerLongShortRatio> newers = new ArrayList<>();

        ExchangeTakerLongShortRatio newer = ls.get(ls.size() - 1);
        Page<ExchangeTakerLongShortRatio> page = find(newer.getExchangeId(), newer.getTradeType(), newer.getSymbol(), newer.getPeriod(), 0, 1, -1, "time");
        if (page.getTotalElements() == 0) {
            newers.addAll(ls);
        } else {
            ExchangeTakerLongShortRatio olderLast = page.getContent().get(0);
            for (int i = ls.size() - 1; i >= 0; --i) {
                ExchangeTakerLongShortRatio n = ls.get(i);
                if (n.getTime() <= olderLast.getTime()) {
                    break;
                }
                newers.add(n);
            }
            Collections.reverse(newers);
        }
        if (newers.size() == 0) {
            return;
        }
        exchangeTakerLongShortRatioDAODynamic.bulkInsert(true, newers);
    }
}
