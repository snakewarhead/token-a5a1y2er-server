package com.cq.exchange.vo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        a.setAction(ExchangeRunningParam.ActionType.ForceOrder, "BTCUSDT", "aaa");
        ExchangeRunningParam b = new ExchangeRunningParam(1, 2);
        b.setAction(ExchangeRunningParam.ActionType.ForceOrder, "BTCUSDT", "aaa");

        log.info("{}", a.equals(b));
        log.info("{} - {}", a.hashCode(), b.hashCode());

        List<String> aa = Arrays.asList("BTC", "ETH");
        List<String> bb = Arrays.asList("BTC", "ETH");

        log.info("{}", aa.equals(bb));

        log.info("{}", 1 << 0);
        log.info("{}", 1 << 1);
        log.info("{}", 1 << 2);

        Assert.assertEquals(a, b);
    }

}