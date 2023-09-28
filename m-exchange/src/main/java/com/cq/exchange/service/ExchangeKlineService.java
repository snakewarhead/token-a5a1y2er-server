package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.exchange.dao.ExchangeKlineDAO;
import com.cq.exchange.dao.ExchangeKlineDAODynamic;
import com.cq.exchange.entity.ExchangeKline;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.mongodb.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ExchangeKlineService extends ExchangeBaseService<ExchangeKline> {

    private final ExchangeKlineDAO exchangeKlineDAO;
    private final ExchangeKlineDAODynamic exchangeKlineDAODynamic;

    public static Query buildQuery(ExchangeKline e) {
        QueryBuilder qb = new QueryBuilder();
        qb.and("exchangeId").is(e.getExchangeId());
        qb.and("tradeType").is(e.getTradeType());
        qb.and("symbol").is(e.getSymbol());
        qb.and("pair").is(e.getPair());

        qb.and("period").is(e.getPeriod());
        qb.and("openTime").is(e.getOpenTime());

        return new BasicQuery(qb.get().toString());
    }

    public void saveNews(List<ExchangeKline> ls) {
        if (CollUtil.isEmpty(ls)) {
            return;
        }
        ExchangeKline temp = ls.get(0);
        ExchangeKline latest = exchangeKlineDAO.findFirstByExchangeIdAndTradeTypeAndSymbolAndPeriodOrderByOpenTimeDesc(temp.getExchangeId(), temp.getTradeType(), temp.getSymbol(), temp.getPeriod());

        if (latest != null) {
            ls = ls.stream().filter(i -> i.getOpenTime() > latest.getOpenTime()).collect(Collectors.toList());
        }
        if (CollUtil.isEmpty(ls)) {
            return;
        }

        List<Pair<Query, ExchangeKline>> updates = ls.stream().map(i -> Pair.of(buildQuery(i), i)).collect(Collectors.toList());
        exchangeKlineDAODynamic.bulkUpsertWrap(false, updates);
    }

    public void updateOne(ExchangeKline e) {
        exchangeKlineDAODynamic.upsertWrap(buildQuery(e), e);
    }

    public List<ExchangeKline> findOlder(Integer exchangeId, Integer tradeType, String symbol, String period, Integer skip, Integer limit) {
        List<ExchangeKline> ls = exchangeKlineDAO.findMore(exchangeId, tradeType, symbol, period, "openTime", -1, skip, limit);
        List<ExchangeKline> lsCopy = new ArrayList<>(ls);
        // klines need to be reverse
        return CollUtil.reverse(lsCopy);
    }

    public ExchangeKline findLatest(Integer exchangeId, Integer tradeType, String symbol, String period) {
        return exchangeKlineDAO.findFirstByExchangeIdAndTradeTypeAndSymbolAndPeriodOrderByOpenTimeDesc(exchangeId, tradeType, symbol, period);
    }

    public long nextPeriod(ExchangeKline k) {
        ExchangePeriodEnum p = ExchangePeriodEnum.getEnum(k.getPeriod());
        return k.getOpenTime() + p.getMillis();
    }
}
