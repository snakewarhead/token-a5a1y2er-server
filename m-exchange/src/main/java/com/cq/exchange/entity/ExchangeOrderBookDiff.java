package com.cq.exchange.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by lin on 2020-11-05.
 *
 * not store in db, but transfer by mq
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeOrderBookDiff extends ExchangeEntity {

    private long updateIdLastLast;
    private long updateIdLast;
    private long updateIdFirst;

    private List<ExchangeOrderBook.Order> asksUpdate;
    private List<ExchangeOrderBook.Order> bidsUpdate;
}
