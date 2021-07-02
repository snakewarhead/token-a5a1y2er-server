package com.cq.exchange.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lin on 2020-11-09.
 */
@Data
public class ExchangeRunningParam implements Serializable {

    private int type;

    private List<Action> actions;

    public static ExchangeRunningParam parse(String m) throws JsonProcessingException {
        return new ObjectMapper().readValue(m, ExchangeRunningParam.class);
    }

    @Data
    public static class Action {
        String name;
        List<String> symbols;
        List<String> params;
    }

}
