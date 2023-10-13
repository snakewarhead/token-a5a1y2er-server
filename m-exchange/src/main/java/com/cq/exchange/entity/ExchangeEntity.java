package com.cq.exchange.entity;

import com.cq.core.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;

@Data
public class ExchangeEntity<T> extends BaseEntity<T> implements Serializable {

    @Indexed
    protected Integer exchangeId;

    @Indexed
    protected Integer tradeType;

    @Indexed
    protected String symbol;

    @Indexed
    protected String pair;

    protected Date dateUpdate = new Date();

}
