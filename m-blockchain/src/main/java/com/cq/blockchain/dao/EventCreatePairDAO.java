package com.cq.blockchain.dao;

import com.cq.blockchain.entity.EventCreatePair;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lin on 2020-09-24.
 */
@Repository
public interface EventCreatePairDAO extends MongoRepository<EventCreatePair, String> {

    EventCreatePair findOneByTransactionHash(String transactionHash);
}
