package com.cq.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@ComponentScan({"com.cq.core", "com.cq.exchange"})
@SpringBootApplication
public class SExchangeGrabberBinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SExchangeGrabberBinanceApplication.class, args);
    }

}
