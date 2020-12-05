package com.cq.exchange.enums;

import java.util.Arrays;

public enum ExchangeTradeType {
    SPOT(1),
    FUTURE_USDT(2),
    FUTURE_COIN(3),
    ;

    private int code;

    ExchangeTradeType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ExchangeTradeType getEnum(int t) {
        return Arrays.stream(ExchangeTradeType.values()).filter(item -> t == item.getCode()).findFirst().orElse(null);
    }
}
