package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeCoinInfoRawDAO;
import com.cq.exchange.dao.ExchangeCoinInfoRawDAODynamic;
import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ExchangeCoinInfoRawService extends ExchangeBaseService<ExchangeCoinInfoRaw> {

    private final ExchangeCoinInfoRawDAO exchangeCoinInfoRawDAO;
    private final ExchangeCoinInfoRawDAODynamic exchangeCoinInfoRawDAODynamic;

    public void saveAll(List<ExchangeCoinInfoRaw> ls) {
        saveAll(exchangeCoinInfoRawDAODynamic, ls);
    }

    public List<ExchangeCoinInfoRaw> find(Integer exchangeId, Integer tradeType, Integer status) {
        return exchangeCoinInfoRawDAO.findByExchangeIdAndTradeTypeAndStatus(exchangeId, tradeType, status);
    }
}
