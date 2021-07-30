package com.cq.exchange.entity;

import com.cq.core.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "exchange_future_perpetual_funding_rate")
public class ExchangeFutureFundingRate extends ExchangeEntity<ExchangeFutureFundingRate> {

  private BigDecimal markPrice;

  private BigDecimal indexPrice;

  private BigDecimal lastFundingRate;

  private Long nextFundingTime;

  private Long time;

  private BigDecimal estimatedRate;
}
