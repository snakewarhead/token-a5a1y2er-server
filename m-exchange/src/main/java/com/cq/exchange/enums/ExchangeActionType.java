package com.cq.exchange.enums;

import java.util.Arrays;

public enum ExchangeActionType {
    OrderBook(1 << 0),
    AggTrade(1 << 1),
    ForceOrder(1 << 2),
    TakerLongShortRatio(1 << 3),

    // ------ 1 << 31
    All(0XFFFF_FFFF);

    ExchangeActionType(int c) {
        this.code = c;
    }

    private int code;

    public boolean is(String name) {
        return All.equals(name) || name().equals(name);
    }

    public static ExchangeActionType getEnum(String n) {
        return Arrays.stream(ExchangeActionType.values()).filter(item -> item.name().equals(n)).findFirst().orElse(null);
    }

    public static boolean contains(String n) {
        return getEnum(n) != null;
    }

}
