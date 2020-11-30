package com.cq.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by lin on 2020-09-24.
 */
public class BaseDAODynamic<T> {

    protected final Class<T> clazz;

    @Autowired
    protected MongoTemplate mongoTemplate;

    public BaseDAODynamic() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        clazz = (Class<T>) type.getActualTypeArguments()[0];
    }

    public Page<T> findByQuery(Query query, Pageable pageable) {
        long total = mongoTemplate.count(query, clazz);

        List<T> ls = mongoTemplate.find(query.with(pageable), clazz);
        return new PageImpl(ls, pageable, total);
    }
}
