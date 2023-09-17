package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeTickerDAO;
import com.cq.exchange.dao.ExchangeTickerDAODynamic;
import com.cq.exchange.entity.ExchangeTicker;
import com.mongodb.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ExchangeTickerService extends ExchangeBaseService<ExchangeTicker> {

    private final ExchangeTickerDAO exchangeTickerDAO;
    private final ExchangeTickerDAODynamic exchangeTickerDAODynamic;

    public void saveAll(List<ExchangeTicker> ls) {
        saveAll(exchangeTickerDAODynamic, ls);
    }
}
