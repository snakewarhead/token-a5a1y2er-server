package com.cq.exchange.enums;

import java.util.Arrays;

import static java.util.concurrent.TimeUnit.*;

public enum ExchangePeriodEnum {
    m1("1m", MINUTES.toMillis(1)),
    m3("3m", MINUTES.toMillis(3)),
    m5("5m", MINUTES.toMillis(5)),
    m15("15m", MINUTES.toMillis(15)),
    m30("30m", MINUTES.toMillis(30)),

    h1("1h", HOURS.toMillis(1)),
    h2("2h", HOURS.toMillis(2)),
    h4("4h", HOURS.toMillis(4)),
    h6("6h", HOURS.toMillis(6)),
    h8("8h", HOURS.toMillis(8)),
    h12("12h", HOURS.toMillis(12)),

    d1("1d", DAYS.toMillis(1)),
    d3("3d", DAYS.toMillis(3)),

    w1("1w", DAYS.toMillis(7)),

    M1("1M", DAYS.toMillis(30));

    private final String symbol;
    private final Long millis;

    ExchangePeriodEnum(String symbol, Long millis) {
        this.millis = millis;
        this.symbol = symbol;
    }

    public Long getMillis() {
        return millis;
    }

    public String code() {
        return symbol;
    }

    public static ExchangePeriodEnum getEnum(String s) {
        return Arrays.stream(ExchangePeriodEnum.values()).filter(i -> i.symbol.equals(s)).findFirst().orElse(null);
    }

}
