package com.cq.exchange.dao;

import com.cq.exchange.entity.ExchangeForceOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lin on 2020-11-10.
 */
@Repository
public interface ExchangeForceOrderDAO extends MongoRepository<ExchangeForceOrder, String> {

}
