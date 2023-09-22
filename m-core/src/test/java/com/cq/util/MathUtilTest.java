package com.cq.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import com.cq.util.MathUtil;

import java.math.BigDecimal;

@Slf4j
public class MathUtilTest {

    @Test
    public void test() {
        BigDecimal n0 = new BigDecimal("0.555666770000");
        String s0 = MathUtil.strip(n0, 4);

        BigDecimal n1 = new BigDecimal("0.555666770000");
        String s1 = MathUtil.strip(n1);

        BigDecimal n2 = new BigDecimal("0.3333");
        String s2 = MathUtil.strip(n2, 3);

        log.info("{} {} {}", s0, s1, s2);
    }
}
