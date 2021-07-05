package com.cq.exchange.vo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lin on 2020-11-09.
 */
@Data
public class ExchangeRunningParam implements Serializable {

    private int exchange;

    private int tradeType;

    private List<Action> actions;

    public ExchangeRunningParam() {
    }

    public ExchangeRunningParam(int exchange, int tradeType) {
        this.exchange = exchange;
        this.tradeType = tradeType;
    }

    public ExchangeRunningParam addAction(ActionType actionType, String symbol, String param) {
        if (actions == null) {
            actions = new ArrayList<>();
        }

        Action a = new Action();
        a.setName(actionType.name());
        a.setSymbols(Arrays.asList(symbol));
        if (StrUtil.isNotEmpty(param)) {
            a.setParams(Arrays.asList(param));
        }

        actions.add(a);

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
        TakerLongShortRatio,
    }

}
