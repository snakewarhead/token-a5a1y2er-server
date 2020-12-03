package com.cq.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Created by lin on 2020-12-01.
 */
@Data
public class BaseEntity<T> {

    @MongoId(FieldType.OBJECT_ID)
    protected String id;

}
