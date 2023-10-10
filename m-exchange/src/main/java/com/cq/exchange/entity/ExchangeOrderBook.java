package com.cq.exchange.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by lin on 2020-11-05.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
        @CompoundIndex(def = "{exchangeId: 1, tradeType: 1, symbol: 1}", unique = true)
})
@Document(collection = "exchange_order_book")
public class ExchangeOrderBook extends ExchangeEntity<ExchangeOrderBook> {

    private Date time;

    private long updateIdLast;

    private List<Order> asks;
    private List<Order> bids;

    @Data
    @AllArgsConstructor
    public static class Order {

        @Indexed
        private BigDecimal price;

        private BigDecimal amount;
    }

}
