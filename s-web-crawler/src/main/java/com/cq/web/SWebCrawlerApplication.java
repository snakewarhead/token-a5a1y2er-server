package com.cq.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@ComponentScan({"com.cq.core", "com.cq.web"})
@SpringBootApplication
public class SWebCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SWebCrawlerApplication.class, args);
    }

}
