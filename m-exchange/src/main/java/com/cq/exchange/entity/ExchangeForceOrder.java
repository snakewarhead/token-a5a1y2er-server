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
@Document(collection = "exchange_force_order")
public class ExchangeForceOrder extends ExchangeEntity<ExchangeForceOrder> {

    private BigDecimal price;
    private BigDecimal avragePrice;
    private BigDecimal origQty;
    private BigDecimal executedQty;

    private String status;
    private String timeInForce;
    private String type;
    private String side;

    private Long time;

}
