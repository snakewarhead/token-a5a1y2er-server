package com.cq.web.dao;

import com.cq.web.entity.CoinNews;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinNewsDAO extends MongoRepository<CoinNews, String> {
}
