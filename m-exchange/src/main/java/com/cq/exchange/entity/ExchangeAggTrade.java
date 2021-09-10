package com.cq.exchange.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by lin on 2020-12-05.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "exchange_agg_trade")
public class ExchangeAggTrade extends ExchangeEntity<ExchangeAggTrade> {

    private Long tradeId;

    private double price;
    private double quantity;

    @Indexed
    private Boolean buyerMaker;

    private Long time;

}
