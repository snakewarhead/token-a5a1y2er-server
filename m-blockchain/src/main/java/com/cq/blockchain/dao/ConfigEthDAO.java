package com.cq.blockchain.dao;

import com.cq.blockchain.entity.ConfigEth;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lin on 2020-09-24.
 */
@Repository
public interface ConfigEthDAO extends MongoRepository<ConfigEth, Integer> {

}
