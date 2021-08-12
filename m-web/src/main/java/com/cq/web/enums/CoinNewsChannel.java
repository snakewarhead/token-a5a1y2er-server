package com.cq.web.enums;

import java.util.Arrays;

public enum CoinNewsChannel {

    BINANCE_ANNOUCEMENT(1)
    ;

    private int code;

    CoinNewsChannel(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String nameLowerCase() {
        return this.name().toLowerCase();
    }

    public boolean is(int c) {
        return this.code == c;
    }

    public boolean isNot(int c) {
        return !(is(c));
    }

    public static CoinNewsChannel getEnum(int c) {
        return Arrays.stream(CoinNewsChannel.values()).filter(item -> c == item.getCode()).findFirst().orElse(null);
    }

    public static CoinNewsChannel getEnum(String n) {
        return Arrays.stream(CoinNewsChannel.values()).filter(item -> item.name().equalsIgnoreCase(n)).findFirst().orElse(null);
    }

    public static boolean contains(int c) {
        return getEnum(c) != null;
    }

}
