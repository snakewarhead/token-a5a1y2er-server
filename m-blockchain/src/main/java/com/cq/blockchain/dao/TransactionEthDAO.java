package com.cq.blockchain.dao;

import com.cq.blockchain.entity.TransactionEth;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lin on 2020-09-24.
 */
@Repository
public interface TransactionEthDAO extends MongoRepository<TransactionEth, String> {

    TransactionEth findOneByTransactionHash(String transactionHash);
}
