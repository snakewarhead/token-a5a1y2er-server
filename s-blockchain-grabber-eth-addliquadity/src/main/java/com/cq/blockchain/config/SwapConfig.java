package com.cq.blockchain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by lin on 2021-03-24.
 */
@Data
@Component
@ConfigurationProperties(prefix = "swap")
public class SwapConfig {

    private String contractAddressRouter;
    private String contractAddressFactory;
    private List<String> liquidityLimites;

}
