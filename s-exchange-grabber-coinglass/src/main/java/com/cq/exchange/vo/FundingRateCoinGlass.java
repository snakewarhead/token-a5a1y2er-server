package com.cq.exchange.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundingRateCoinGlass {
    int status;
    BigDecimal cIndexPrice;
}
