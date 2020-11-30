package com.cq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "exchange_future_perpetual_funding_rate")
public class ExchangeFutureFundingRate {

  @MongoId(FieldType.OBJECT_ID)
  private String id;

  @Indexed
  private Integer exchangeId;

  @Indexed
  private Integer futureType;

  @Indexed
  private Integer settleType;

  @Indexed
  private String name;

  @Indexed
  private String symbol;

  private String pair;

  private BigDecimal markPrice;

  private BigDecimal indexPrice;

  private BigDecimal lastFundingRate;

  private Long nextFundingTime;

  private Long time;

  private BigDecimal estimatedRate;
}
