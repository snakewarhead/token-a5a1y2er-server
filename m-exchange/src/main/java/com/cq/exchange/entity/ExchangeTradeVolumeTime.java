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
@Document(collection = "exchange_trade_volume_time")
public class ExchangeTradeVolumeTime extends BaseEntity<ExchangeTradeVolumeTime> {

    @Indexed
    private Integer exchangeId;

    @Indexed
    private Integer tradeType;

    @Indexed
    private String symbol;

    @Indexed
    private String pair;

    @Indexed
    private String period;

    @Indexed
    private Date time;

    private BigDecimal qtyBuyerTotal;
    private BigDecimal qtyBuyerSmall;
    private BigDecimal qtyBuyerMiddle;
    private BigDecimal qtyBuyerBig;

    private BigDecimal qtySellerTotall;
    private BigDecimal qtySellerSmall;
    private BigDecimal qtySellerMiddle;
    private BigDecimal qtySellerBig;

}
