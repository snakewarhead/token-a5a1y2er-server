package com.cq.exchange.dao;

import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lin on 2020-11-10.
 */
@Repository
public interface ExchangeCoinInfoRawDAO extends MongoRepository<ExchangeCoinInfoRaw, String> {

}
