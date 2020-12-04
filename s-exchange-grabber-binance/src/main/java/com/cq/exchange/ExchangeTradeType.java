package com.cq.exchange;

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
        ExchangeTradeType[] arry = ExchangeTradeType.values();
        for (int i = 0; i < arry.length; i++) {
            if (arry[i].getCode() == t) {
                return arry[i];
            }
        }
        return null;
    }
}
