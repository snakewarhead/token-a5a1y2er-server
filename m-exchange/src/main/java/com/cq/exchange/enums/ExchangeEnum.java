package com.cq.exchange.enums;

import java.util.Arrays;

/**
 * Created by lin on 2020-12-05.
 */
public enum ExchangeEnum {

    BINANCE(1),
    OKEX(2),
    HUOBI(3),
    GATEIO(4),
    ;

    private int code;

    ExchangeEnum(int code) {
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

    public static ExchangeEnum getEnum(int c) {
        return Arrays.stream(ExchangeEnum.values()).filter(item -> c == item.getCode()).findFirst().orElse(null);
    }

    public static boolean contains(int c) {
        return getEnum(c) != null;
    }
}
