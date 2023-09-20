package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeCoinInfoDAO;
import com.cq.exchange.entity.ExchangeCoinInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Created by lin on 2020-11-10.
 */
@Service
@RequiredArgsConstructor
public class ExchangeCoinInfoService {

    private final ExchangeCoinInfoDAO exchangeCoinInfoDAO;

    public void save(ExchangeCoinInfo e) {
        ExchangeCoinInfo old = exchangeCoinInfoDAO.findByExchangeIdAndTradeTypeAndSymbolAndPeriod(e.getExchangeId(), e.getTradeType(), e.getSymbol(), e.getPeriod());
        if (old != null) {
            e.setId(old.getId());
        }
        exchangeCoinInfoDAO.save(e);
    }

    public ExchangeCoinInfo find(Integer exchangeId, Integer tradeType, String symbol,String period) {
        return exchangeCoinInfoDAO.findByExchangeIdAndTradeTypeAndSymbolAndPeriod(exchangeId, tradeType, symbol, period);
    }
}
