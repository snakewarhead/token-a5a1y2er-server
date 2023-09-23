package com.cq.exchange.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    public static String host;
    public static int portHttp;
    public static int portSocks;

    @Value("${proxy.host}")
    public void setHost(String host) {
        Config.host = host;
    }

    @Value("${proxy.port.http}")
    public void setPortHttp(int portHttp) {
        Config.portHttp = portHttp;
    }

    @Value("${proxy.port.socks}")
    public void setPortSocks(int portSocks) {
        Config.portSocks = portSocks;
    }
}
