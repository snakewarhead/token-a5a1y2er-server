package com.cq.exchange.enums;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

import java.util.Arrays;
import java.util.Date;

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

    private final int num;
    private final String unit;

    ExchangePeriodEnum(String symbol, Long millis) {
        this.millis = millis;
        this.symbol = symbol;

        num = Integer.parseInt(symbol.substring(0, symbol.length() - 1));
        unit = symbol.substring(symbol.length() - 1);
    }

    public String getSymbol() {
        return symbol;
    }

    public Long getMillis() {
        return millis;
    }

    public int getNum() {
        return num;
    }

    public String getUnit() {
        return unit;
    }

    public boolean is(ExchangePeriodEnum e) {
        return this.symbol.equals(e.symbol);
    }

    public static ExchangePeriodEnum getEnum(String s) {
        return Arrays.stream(ExchangePeriodEnum.values()).filter(i -> i.symbol.equals(s)).findFirst().orElse(null);
    }

    public Date beginOfInterval(long current) {
        Date dateCurr = new Date(current);
        if ("m".equals(unit)) {
            int c = DateUtil.minute(dateCurr);
            long begin = c / num * millis;
            Date dateTruncate = DateUtil.truncate(dateCurr, DateField.HOUR_OF_DAY);
            Date dateBegin = new Date(dateTruncate.getTime() + begin);
            return dateBegin;
        } else if ("h".equals(unit)) {
            int c = DateUtil.hour(dateCurr, true);
            long begin = c / num * millis;
            Date dateTruncate = DateUtil.truncate(dateCurr, DateField.DAY_OF_MONTH);
            Date dateBegin = new Date(dateTruncate.getTime() + begin);
            return dateBegin;
        } else {
            throw new RuntimeException("symbol is not support");
        }
    }

    public Date beforeOfInterval(long current) {
        Date dateBegin = beginOfInterval(current);
        if ("h".equals(unit)) {
            return DateUtil.offsetHour(dateBegin, -num);
        } else {
            throw new RuntimeException("symbol is not support");
        }
    }
}
