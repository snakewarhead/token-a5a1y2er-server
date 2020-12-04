package com.cq.exchange;

import lombok.Data;

import java.util.List;

/**
 * Created by lin on 2020-11-09.
 */
@Data
public class ExchangeRunningParam {

    private int type;

    private List<Action> actions;

    @Data
    public static class Action {
        String name;
        List<String> symbols;
    }
}
