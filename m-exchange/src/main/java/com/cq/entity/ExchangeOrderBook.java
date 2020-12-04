package com.cq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Document(collection = "exchange_order_book")
public class ExchangeOrderBook extends BaseEntity<ExchangeOrderBook> {

    @Indexed
    private Integer exchangeId;

    @Indexed
    private Integer tradeType;

    @Indexed
    private String symbol;

    private String pair;

    private Date time;

    private List<Order> asks;
    private List<Order> bids;

    private List<Order> asksUpdate;
    private List<Order> bidsUpdate;

    @Data
    @AllArgsConstructor
    public static class Order {

        @Indexed
        private BigDecimal price;

        private BigDecimal amount;
    }

}
