package com.cq.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@ComponentScan({"com.cq.core", "com.cq.blockchain"})
@SpringBootApplication
public class SBlockchainGrabberEthUtilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SBlockchainGrabberEthUtilityApplication.class, args);
    }

}
