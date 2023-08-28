package com.cq.exchange.vo.fundingrate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CMarginListItem {

    @JsonProperty("rate")
    private BigDecimal rate;

    @JsonProperty("nextFundingTime")
    private long nextFundingTime;

    @JsonProperty("exchangeName")
    private String exchangeName;

    @JsonProperty("predictedRate")
    private BigDecimal predictedRate;

	@JsonProperty("exchangeLogo")
	private String exchangeLogo;

    @JsonProperty("status")
    private int status;
}