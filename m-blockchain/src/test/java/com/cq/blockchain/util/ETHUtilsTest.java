package com.cq.blockchain.util;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by lin on 2020-09-23.
 */
public class ETHUtilsTest {

    private ETHUtils u;

    @Before
    public void setUp() throws Exception {
        u = new ETHUtils("", "", "192.168.1.102", "7000", "qwert123456");
    }

    @Test
    public void validateaddress() {
        System.out.println(u.validateaddress("ljaskdjfjldsk123"));
    }

    @Test
    public void eth_blockNumberValue() throws Exception {
        System.out.println(u.eth_blockNumberValue());
    }
}