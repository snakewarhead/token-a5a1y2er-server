package com.cq.exchange.vo.fundingrate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class DataItem {

    @JsonProperty("uMarginList")
    private List<UMarginListItem> uMarginList;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("cMarginList")
    private List<CMarginListItem> cMarginList;

    @JsonIgnore
    @JsonProperty("symbolLogo")
	private String symbolLogo;

    @JsonProperty("status")
    private int status;

    @JsonProperty("uIndexPrice")
    private BigDecimal uIndexPrice;

    @JsonProperty("uPrice")
    private BigDecimal uPrice;

    @JsonProperty("cPrice")
    private BigDecimal cPrice;

    @JsonProperty("cIndexPrice")
    private BigDecimal cIndexPrice;
}