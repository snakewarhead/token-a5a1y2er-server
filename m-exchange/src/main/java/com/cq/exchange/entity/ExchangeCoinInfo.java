package com.cq.exchange.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "exchange_coin_info")
public class ExchangeCoinInfo extends ExchangeEntity<ExchangeCoinInfo> {

    private BigDecimal qtyAvgOrderBookBid;
    private BigDecimal qtyAvgOrderBookAsk;

    private BigDecimal priceAvgTrade;
    private BigDecimal qtyAvgTradeBuy;
    private BigDecimal qtyAvgTradeSell;
    private BigDecimal qtyStdevTradeSell;
    private BigDecimal qtyStdevTradeBuy;

}
