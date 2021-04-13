package com.cq.blockchain.dao;

import com.cq.blockchain.entity.EventApproval;
import com.cq.blockchain.entity.EventCreatePair;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lin on 2020-09-24.
 */
@Repository
public interface EventApprovalDAO extends MongoRepository<EventApproval, String> {

    EventApproval findOneByTransactionHash(String transactionHash);

    EventApproval findOneByToken(String token);
}
