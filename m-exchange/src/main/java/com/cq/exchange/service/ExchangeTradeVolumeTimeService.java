package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeTradeVolumeTimeDAO;
import com.cq.exchange.dao.ExchangeTradeVolumeTimeDAODynamic;
import com.cq.exchange.entity.ExchangeTradeVolumeTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by lin on 2020-11-10.
 */
@Service
@RequiredArgsConstructor
public class ExchangeTradeVolumeTimeService {

    private final ExchangeTradeVolumeTimeDAO exchangeTradeVolumeTimeDAO;
    private final ExchangeTradeVolumeTimeDAODynamic exchangeTradeVolumeTimeDAODynamic;

    public ExchangeTradeVolumeTime find(Integer exchangeId, Integer tradeType, String symbol, String period, Date time) {
        return exchangeTradeVolumeTimeDAO.findByExchangeIdAndTradeTypeAndSymbolAndPeriodAndTime(
                exchangeId,
                tradeType,
                symbol,
                period,
                time
        );
    }
}
