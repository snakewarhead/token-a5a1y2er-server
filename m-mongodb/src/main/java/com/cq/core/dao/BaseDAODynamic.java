package com.cq.core.dao;

import com.cq.core.entity.BaseEntity;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;

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

    public boolean upsert(Query q, Update u) {
        UpdateResult res = mongoTemplate.upsert(q, u, clazz);
        return res.wasAcknowledged();
    }

    public boolean upsertWrap(Query q, T t) {
        org.bson.Document d = (org.bson.Document) mongoTemplate.getConverter().convertToMongoType(t);
        Update u = Update.fromDocument(d);
        return upsert(q, u);
    }

    public int bulkInsert(boolean isOrdered, List<T> ls) {
        BulkOperations.BulkMode m = isOrdered ? BulkOperations.BulkMode.ORDERED : BulkOperations.BulkMode.UNORDERED;
        BulkWriteResult res = mongoTemplate.bulkOps(m, clazz).insert(ls).execute();

        if (!res.wasAcknowledged()) {
            return 0;
        }
        return res.getInsertedCount();
    }

    public int bulkUpsert(boolean isOrdered, List<Pair<Query, Update>> updates) {
        BulkOperations.BulkMode m = isOrdered ? BulkOperations.BulkMode.ORDERED : BulkOperations.BulkMode.UNORDERED;
        BulkWriteResult res = mongoTemplate.bulkOps(m, clazz).upsert(updates).execute();

        if (!res.wasAcknowledged()) {
            return 0;
        }
        return res.getUpserts().size() + res.getModifiedCount();
    }

    public int bulkUpsertWrap(boolean isOrdered, List<Pair<Query, T>> updates) {
        List<Pair<Query, Update>> uw = updates.stream().map(i -> {
            org.bson.Document d = (org.bson.Document) mongoTemplate.getConverter().convertToMongoType(i.getSecond());
            org.bson.Document du = new org.bson.Document();
            du.append("$set", d);
            Update u = Update.fromDocument(du);
            return Pair.of(i.getFirst(), u);
        }).collect(Collectors.toList());
        return bulkUpsert(isOrdered, uw);
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
