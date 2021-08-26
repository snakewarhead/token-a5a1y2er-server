package com.cq.exchange.enums;

import cn.hutool.core.date.DateUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ExchangePeriodEnumTest {

    @Test
    public void beginOfIntervalTest() {
        Date begin = ExchangePeriodEnum.m15.beginOfInterval(new Date().getTime());
        Assert.assertNotNull(begin);

        Date before = ExchangePeriodEnum.h4.beforeOfInterval(DateUtil.parse("2021-08-16 00:05:00").getTime());
        Assert.assertNotNull(before);
    }
}