package com.cq.exchange.vo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lin on 2020-11-09.
 */
@Data
public class ExchangeRunningParam implements Serializable {

    private int exchange;

    private int tradeType;

    private Action action;

    public ExchangeRunningParam() {
    }

    public ExchangeRunningParam(int exchange, int tradeType) {
        this.exchange = exchange;
        this.tradeType = tradeType;
    }

    public ExchangeRunningParam setAction(ActionType actionType, String symbol, String param) {
        if (action != null) {
            throw new RuntimeException("action is running");
        }

        action = new Action();
        action.setName(actionType.name());
        action.setSymbols(Arrays.asList(symbol));
        if (StrUtil.isNotEmpty(param)) {
            action.setParams(Arrays.asList(param));
        }

        return this;
    }

    public static ExchangeRunningParam parse(String m) throws JsonProcessingException {
        return new ObjectMapper().readValue(m, ExchangeRunningParam.class);
    }

    @Data
    public static class Action {
        String name;
        List<String> symbols;
        List<String> params;
    }

    public enum ActionType {
        OrderBook,
        AggTrade,
        ForceOrder,
        TakerLongShortRatio;

        public boolean is(String name) {
            return name().equals(name);
        }
    }

}
