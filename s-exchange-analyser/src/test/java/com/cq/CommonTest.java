package com.cq;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
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

    @Test
    public void mapRemove() {
        Map<String, Long> m = new HashMap<>() {
            {
                put("aaa", 111L);
                put("bbb", 222L);
                put("ccc", 333L);
            }
        };
//        for (Map.Entry<String, Long> e : m.entrySet()) {
//            if ("ccc".equals(e.getKey())) {
//                m.remove(e.getKey());
//            }
//        }
        m.values().removeIf(v -> v < 333L);

        log.info(m.toString());
    }
}
