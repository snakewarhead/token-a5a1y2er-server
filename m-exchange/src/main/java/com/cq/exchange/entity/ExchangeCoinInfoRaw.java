package com.cq.exchange.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
        @CompoundIndex(def = "{exchangeId: 1, tradeType: 1, symbol: 1}", unique = true)
})
@Document(collection = "exchange_coin_info_raw")
public class ExchangeCoinInfoRaw extends ExchangeEntity<ExchangeCoinInfoRaw> {
    /**
     * 0 - disable, 1 - trading
     */
    private int status;

    private BigDecimal tradingFee;
    private Integer pricePrecision;
    private BigDecimal priceMax;
    private Integer quantityPrecision;
    private BigDecimal quantityMin;
    private BigDecimal quantityMax;
    private BigDecimal quantityStep;
    private BigDecimal amountMin;
}
