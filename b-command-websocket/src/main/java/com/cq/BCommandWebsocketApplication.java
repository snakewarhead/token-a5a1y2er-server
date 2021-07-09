package com.cq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@ComponentScan({"com.cq", "com.cq.core"})
@SpringBootApplication
public class BCommandWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(BCommandWebsocketApplication.class, args);
    }

}
