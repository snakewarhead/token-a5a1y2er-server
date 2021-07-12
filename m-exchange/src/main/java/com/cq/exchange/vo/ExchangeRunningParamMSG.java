package com.cq.exchange.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExchangeRunningParamMSG implements Serializable {

    public final String SUBSCRIBE = "subscribe";
    public final String UNSUBSCRIBE = "unsubscribe";

    private String subscribe;

    private ExchangeRunningParam param;

    public boolean isSubscribe() {
        return SUBSCRIBE.equals(subscribe);
    }
}
