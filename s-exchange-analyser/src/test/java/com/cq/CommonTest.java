package com.cq;

import cn.hutool.core.date.DateUtil;
import org.junit.Assert;
import org.junit.Test;

public class CommonTest {

    @Test
    public void LongCompare() {
        Long a = DateUtil.current(false);
        Long b = a + 4 * 60 * 1000;
        Assert.assertTrue(a < b);
        Assert.assertTrue(a.compareTo(b) < 0);

        Long c = 109283901283L;
        Long d = c - 120398L;
        Assert.assertTrue(c > d);
        Assert.assertTrue(c.compareTo(d) > 0);

        Long e = c;
        Assert.assertTrue(c == e);
        Assert.assertTrue(c.compareTo(e) == 0);
    }
}
