package com.cq.exchange.enums;

import java.util.Arrays;

public enum ExchangeTradeType {
    ALL(0),
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

    public boolean is(int c) {
        return this.code == c;
    }

    public boolean isNot(int c) {
        return !(is(c));
    }

    public boolean isFuture() {
        return code > 1;
    }

    public static ExchangeTradeType getEnum(int c) {
        return Arrays.stream(ExchangeTradeType.values()).filter(item -> c == item.getCode()).findFirst().orElse(null);
    }

    public static boolean contains(int c) {
        return getEnum(c) != null;
    }
}
