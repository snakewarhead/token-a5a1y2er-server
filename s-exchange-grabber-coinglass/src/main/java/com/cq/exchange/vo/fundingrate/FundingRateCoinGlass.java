package com.cq.exchange.vo.fundingrate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FundingRateCoinGlass {

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("code")
    private String code;

    @JsonProperty("data")
    private List<DataItem> data;

    @JsonProperty("success")
    private boolean success;

}