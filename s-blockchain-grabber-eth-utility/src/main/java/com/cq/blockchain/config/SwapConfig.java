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

    private List<String> whaleAddresses;

}
