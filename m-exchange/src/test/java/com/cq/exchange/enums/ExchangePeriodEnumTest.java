package com.cq.exchange.enums;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ExchangePeriodEnumTest {

    @Test
    public void beginOfIntervalTest() {
        Date begin = ExchangePeriodEnum.h4.beginOfInterval(new Date().getTime());
        Assert.assertNotNull(begin);
    }
}