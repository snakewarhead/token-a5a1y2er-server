package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeTickerDAO;
import com.cq.exchange.dao.ExchangeTickerDAODynamic;
import com.cq.exchange.entity.ExchangeTicker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ExchangeTickerService {

    private final ExchangeTickerDAO exchangeTickerDAO;
    private final ExchangeTickerDAODynamic exchangeTickerDAODynamic;

    public void saveAll(List<ExchangeTicker> ls) {

    }
}
