package com.cq.exchange.enums;

import java.util.Arrays;

public enum ExchangeActionType {
    // grabber
    OrderBook(1 << 0),
    AggTrade(1 << 1),
    ForceOrder(1 << 2),
    TakerLongShortRatio(1 << 3),
    FundingRate(1 << 4),
    AllTicker(1 << 5),
    KLine(1 << 6),
    CoinInfoRaw(1 << 7),

    // analyser
    CoinInfoShort(1 << 16),
    CoinInfoLong(1 << 17),
    TradeVolumeTime(1 << 18),
    TradeVolumePrice(1 << 19),
    FundingRateRank(1 << 20),
    VolumeChangeQuick(1 << 21),

    // ------ 1 << 31
    All(0XFFFF_FFFF);

    ExchangeActionType(int c) {
        this.code = c;
    }

    private int code;

    public boolean is(String name) {
        return All.equals(name) || name().equals(name);
    }

    public boolean isNot(String name) {
        return !is(name);
    }

    public boolean isGrabber() {
        return code > 0 && code < CoinInfoShort.code;
    }

    public static ExchangeActionType getEnum(String n) {
        return Arrays.stream(ExchangeActionType.values()).filter(item -> item.name().equals(n)).findFirst().orElse(null);
    }

    public static boolean contains(String n) {
        return getEnum(n) != null;
    }

}
