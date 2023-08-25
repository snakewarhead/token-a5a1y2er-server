package com.cq.exchange.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Config {
    @Value("${coinglass.url}")
    private String url;
    @Value("${coinglass.api.secret}")
    private String apiSecret;
}
