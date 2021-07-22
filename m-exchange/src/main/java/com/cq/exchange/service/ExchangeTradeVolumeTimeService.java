package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.exchange.dao.ExchangeTradeVolumeTimeDAO;
import com.cq.exchange.dao.ExchangeTradeVolumeTimeDAODynamic;
import com.cq.exchange.entity.ExchangeTradeVolumeTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    public ExchangeTradeVolumeTime findLast(Integer exchangeId, Integer tradeType, String symbol, String period) {
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "time");
        Page<ExchangeTradeVolumeTime> p = exchangeTradeVolumeTimeDAO.findByExchangeIdAndTradeTypeAndSymbol(exchangeId, tradeType, symbol, period, pageable);
        List<ExchangeTradeVolumeTime> ls = p.getContent();
        if (CollUtil.isEmpty(ls)) {
            return null;
        }
        return ls.get(0);
    }
}
