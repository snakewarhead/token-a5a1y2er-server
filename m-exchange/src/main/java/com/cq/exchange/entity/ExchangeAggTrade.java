package com.cq.exchange.entity;

import com.cq.core.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by lin on 2020-12-05.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "exchange_agg_trade")
public class ExchangeAggTrade extends BaseEntity<ExchangeAggTrade> {

    @Indexed
    private Integer exchangeId;

    @Indexed
    private Integer tradeType;

    @Indexed
    private String symbol;

    @Indexed
    private String pair;

    private Long tradeId;

    private BigDecimal price;
    private BigDecimal quantity;

    @Indexed
    private Boolean buyerMaker;

    private Long time;

}
