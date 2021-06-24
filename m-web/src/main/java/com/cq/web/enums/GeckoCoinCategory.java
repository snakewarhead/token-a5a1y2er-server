package com.cq.web.enums;

import java.util.Arrays;

public enum GeckoCoinCategory {

    DECENTRALIZED_FINANCE_DEFI(1, "decentralized-finance-defi");

    private int code;
    private String symbol;

    GeckoCoinCategory(int code, String symbol) {
        this.code = code;
        this.symbol = symbol;
    }

    public int getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean is(int c) {
        return this.code == c;
    }

    public boolean isNot(int c) {
        return !is(c);
    }

    public static GeckoCoinCategory getEnum(int c) {
        return Arrays.stream(GeckoCoinCategory.values()).filter(i -> i.code == c).findFirst().orElse(null);
    }

    public static String map(int c) {
        GeckoCoinCategory e = getEnum(c);
        if (e == null) {
            throw new RuntimeException("code is error");
        }
        return e.symbol;
    }
}
