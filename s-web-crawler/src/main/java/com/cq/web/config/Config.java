package com.cq.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by lin on 2021-03-24.
 */
@Data
@Component
@ConfigurationProperties(prefix = "web")
public class Config {

}
