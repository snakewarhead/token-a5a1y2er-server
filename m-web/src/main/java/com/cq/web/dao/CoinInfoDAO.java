package com.cq.web.dao;

import com.cq.web.entity.CoinInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinInfoDAO extends MongoRepository<CoinInfo, String> {
}
