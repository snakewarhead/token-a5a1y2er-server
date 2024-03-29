package com.cq.exchange.vo;

import com.cq.exchange.enums.ExchangeActionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ExchangeRunningParamTest {

    @Before
    public void setup() {

    }

    @Test
    public void testEquals() {
        ExchangeRunningParam a = new ExchangeRunningParam(1, 2);
        a.putAction(ExchangeActionType.ForceOrder, "BTCUSDT", "aaa");
        ExchangeRunningParam b = new ExchangeRunningParam(1, 2);
        b.putAction(ExchangeActionType.ForceOrder, "BTCUSDT", "aaa");

        log.info("{}", a.equals(b));
        log.info("{} - {}", a.hashCode(), b.hashCode());
        Assert.assertEquals(a, b);

        List<String> aa = Arrays.asList("BTC", "ETH");
        List<String> bb = Arrays.asList("BTC", "ETH");
        log.info("{}", aa.equals(bb));

        log.info("{}", 1 << 0);
        log.info("{}", 1 << 1);
        log.info("{}", 1 << 2);
    }

    @Test
    public void testExchangeRunningParamMSG() throws JsonProcessingException {
        ExchangeRunningParamMSG msg = new ExchangeRunningParamMSG();
        msg.setSubscribe("subscribe");

        ExchangeRunningParam c = new ExchangeRunningParam(1, 2);
        c.putAction(ExchangeActionType.TakerLongShortRatio, "BTCUSDT", "5m");
        List<String> ls = new ArrayList<>();
        ls.add("5m");
        ls.add("15m");
        c.getAction().setParams(ls);

        String json0 = new ObjectMapper().writeValueAsString(c);
        log.info("{}", json0);

        msg.setParam(c);
        String json = new ObjectMapper().writeValueAsString(msg);
        log.info("{}", json);

        ExchangeRunningParamMSG msgCopy = ExchangeRunningParamMSG.parse(json);
        log.info("{}", msgCopy);
    }

}