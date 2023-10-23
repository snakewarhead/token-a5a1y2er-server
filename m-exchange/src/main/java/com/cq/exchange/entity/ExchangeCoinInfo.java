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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
        @CompoundIndex(def = "{exchangeId: 1, tradeType: 1, symbol: 1, period: 1}", unique = true)
})
@Document(collection = "exchange_coin_info")
public class ExchangeCoinInfo extends ExchangeEntity<ExchangeCoinInfo> {

    @Indexed
    private String period;

    private BigDecimal qtyAvgOrderBookBid;
    private BigDecimal qtyAvgOrderBookAsk;

    private BigDecimal qtyMaxTradeBuy;
    private BigDecimal qtyMaxTradeSell;

    private BigDecimal priceAvgTrade;
    private BigDecimal qtyAvgTradeBuy;
    private BigDecimal qtyAvgTradeSell;
    private BigDecimal qtyStdevTradeSell;
    private BigDecimal qtyStdevTradeBuy;

    private BigDecimal qtyStdevVolume;
    private BigDecimal qtyStdevVolumeRate;  // stdev / mean
    private BigDecimal qtyAvgSmoothVolume;  // mean of volume filtered by stdev * multiple
    private BigDecimal multipleVolume;  // kline.close / qtyAvgSmoothVolume

    private BigDecimal qtyAvgVolumeQuote;

    private BigDecimal avgPriceVolatilityRate;

    private BigDecimal priceDiffFutureAndSpot;

}
