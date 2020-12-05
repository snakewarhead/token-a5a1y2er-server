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

    public ExchangeEnum setCode(int code) {
        this.code = code;
        return this;
    }

    public static ExchangeEnum getEnum(int t) {
        return Arrays.stream(ExchangeEnum.values()).filter(item -> t == item.getCode()).findFirst().orElse(null);
    }

}
