package com.cq.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@ComponentScan({"com.cq.core", "com.cq.exchange"})
@SpringBootApplication
public class SExchangeGrabberCoinGlassApplication {

    public static void main(String[] args) {
        SpringApplication.run(SExchangeGrabberCoinGlassApplication.class, args);
    }

}
