package com.cq.dao;

import com.cq.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by lin on 2020-09-24.
 */
@Slf4j
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

    public void createView(Class<? extends BaseEntity<?>> viewIn, Class<? extends BaseEntity<?>> viewOn, String pipeline) {
        Document docIn = viewIn.getAnnotation(Document.class);
        Assert.notNull(docIn, "Error - @Document annotation is null for viewIn model class: " + viewIn.getSimpleName());

        createView(docIn.collection(), viewOn, pipeline);
    }

    public void createView(String viewName, Class<? extends BaseEntity<?>> viewOn, String pipeline) {
        try {
            if (mongoTemplate.collectionExists(viewName)) {
                log.info("Warn - view {} is already created.", viewName);
                return;
            }

            Document docOn = viewOn.getAnnotation(Document.class);
            Assert.notNull(docOn, "Error - @Document annotation is null for viewOn model class: " + viewOn.getSimpleName());

            org.bson.Document result = mongoTemplate.executeCommand("{" +
                    "create: '" + viewName + "', " +
                    "viewOn: '" + docOn.collection() + "', " +
                    "pipeline: " + pipeline +
                    "}");

            log.info("createView ---- ", result.toJson());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
