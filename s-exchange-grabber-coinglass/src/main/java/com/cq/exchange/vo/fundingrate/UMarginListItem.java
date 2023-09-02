package com.cq.exchange.vo.fundingrate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UMarginListItem {

    @JsonProperty("rate")
    private BigDecimal rate;

    @JsonProperty("nextFundingTime")
    private long nextFundingTime;

    @JsonProperty("exchangeName")
    private String exchangeName;

    @JsonIgnore
    private String exchangeLogo;

    @JsonProperty("status")
    private int status;

    @JsonProperty("predictedRate")
    private BigDecimal predictedRate;
}