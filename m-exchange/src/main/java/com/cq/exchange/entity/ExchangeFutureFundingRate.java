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
        @CompoundIndex(def = "{exchangeId: 1, tradeType: 1, symbol: 1, pair: 1, time: 1}")
})
@Document(collection = "exchange_future_perpetual_funding_rate")
public class ExchangeFutureFundingRate extends ExchangeEntity<ExchangeFutureFundingRate> {

    private BigDecimal markPrice;

    private BigDecimal indexPrice;

    private BigDecimal lastFundingRate;

    private Long nextFundingTime;

    private Long time;

    private BigDecimal estimatedRate;
}
