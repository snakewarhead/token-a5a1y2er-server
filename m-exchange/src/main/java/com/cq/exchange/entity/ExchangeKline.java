package com.cq.exchange.entity;

import com.cq.util.MathUtil;
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
        @CompoundIndex(def = "{exchangeId: 1, tradeType: 1, symbol: 1, period: 1, openTime: 1}", unique = true)
})
@Document(collection = "exchange_kline")
public class ExchangeKline extends ExchangeEntity<ExchangeKline> {

    @Indexed
    private String period;

    @Indexed
    private long openTime;
    @Indexed
    private long closeTime;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private BigDecimal quoteVolume;
    private long numberOfTrades;
    private BigDecimal takerBuyBaseVolume;
    private BigDecimal takerBuyQuoteVolume;

    public BigDecimal calcTakerVolumeDiff() {
        BigDecimal takerSellQuoteVolume = MathUtil.sub(quoteVolume, takerBuyQuoteVolume);
        return MathUtil.sub(takerBuyQuoteVolume, takerSellQuoteVolume);
    }
}
