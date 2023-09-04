package com.cq.exchange.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ExchangeRunningParamMSG implements Serializable {

    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";

    private String subscribe;

    private ExchangeRunningParam param;

    public boolean isSubscribe() {
        return SUBSCRIBE.equals(subscribe);
    }

    public static ExchangeRunningParamMSG parse(String m) throws JsonProcessingException {
        return new ObjectMapper().readValue(m, ExchangeRunningParamMSG.class);
    }
}
