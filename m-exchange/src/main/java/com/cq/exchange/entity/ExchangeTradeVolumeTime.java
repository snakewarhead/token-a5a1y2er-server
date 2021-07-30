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
public class ExchangeTradeVolumeTime extends ExchangeEntity<ExchangeTradeVolumeTime> {

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

    public void reset() {
        qtyBuyerTotal = BigDecimal.ZERO;
        qtyBuyerSmall = BigDecimal.ZERO;
        qtyBuyerMiddle = BigDecimal.ZERO;
        qtyBuyerBig = BigDecimal.ZERO;

        qtySellerTotall = BigDecimal.ZERO;
        qtySellerSmall = BigDecimal.ZERO;
        qtySellerMiddle = BigDecimal.ZERO;
        qtySellerBig = BigDecimal.ZERO;
    }
}
