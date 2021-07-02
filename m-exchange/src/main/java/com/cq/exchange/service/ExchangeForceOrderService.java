package com.cq.exchange.service;

import com.cq.exchange.dao.ExchangeForceOrderDAO;
import com.cq.exchange.entity.ExchangeForceOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Created by lin on 2020-11-10.
 */
@Service
@RequiredArgsConstructor
public class ExchangeForceOrderService {

    private final ExchangeForceOrderDAO exchangeForceOrderDAO;

    public void save(ExchangeForceOrder e) {
        exchangeForceOrderDAO.save(e);
    }

}
