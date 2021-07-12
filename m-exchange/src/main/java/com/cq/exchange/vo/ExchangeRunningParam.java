package com.cq.exchange.vo;

import cn.hutool.core.util.StrUtil;
import com.cq.exchange.enums.ExchangeActionType;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    public ExchangeRunningParam setAction(ExchangeActionType actionType, String symbol, String param) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRunningParam that = (ExchangeRunningParam) o;
        return exchange == that.exchange && tradeType == that.tradeType && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchange, tradeType, action);
    }

    @Override
    public String toString() {
        return "ExchangeRunningParam{" +
                "exchange=" + ExchangeEnum.getEnum(exchange).name() +
                ", tradeType=" + ExchangeTradeType.getEnum(tradeType).name() +
                ", action=" + action +
                '}';
    }

    public static ExchangeRunningParam parse(String m) throws JsonProcessingException {
        return new ObjectMapper().readValue(m, ExchangeRunningParam.class);
    }

    @Data
    public static class Action implements Serializable {
        String name;
        List<String> symbols;
        List<String> params;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Action action = (Action) o;
            return name.equals(action.name) && symbols.equals(action.symbols) && Objects.equals(params, action.params);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, symbols, params);
        }

        @Override
        public String toString() {
            return "Action{" +
                    "name='" + name + '\'' +
                    ", symbols=" + symbols +
                    ", params=" + params +
                    '}';
        }
    }

}
