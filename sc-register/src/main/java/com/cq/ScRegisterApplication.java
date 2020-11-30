package com.cq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ScRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScRegisterApplication.class, args);
    }

}
