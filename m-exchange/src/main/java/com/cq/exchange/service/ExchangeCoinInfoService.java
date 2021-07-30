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
        exchangeCoinInfoDAO.save(e);
    }

}
