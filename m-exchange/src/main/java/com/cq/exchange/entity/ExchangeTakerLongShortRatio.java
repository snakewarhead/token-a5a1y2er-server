package com.cq.exchange.entity;

import com.cq.core.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * Created by lin on 2020-12-05.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "exchange_taker_long_short_ratio")
public class ExchangeTakerLongShortRatio extends BaseEntity<ExchangeTakerLongShortRatio> {

    @Indexed
    private Integer exchangeId;

    @Indexed
    private Integer tradeType;

    @Indexed
    private String symbol;

    private String pair;

    private String baseSymbol;

    private BigDecimal buySellRatio;
    private BigDecimal buyVol;
    private BigDecimal sellVol;

    @Indexed
    private Long time;

}
