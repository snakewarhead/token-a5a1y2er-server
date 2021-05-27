package com.cq.web.task;

import com.cq.web.config.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Created by lin on 2020-09-23.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ApplicationStartup implements ApplicationRunner {

    private final Config config;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        String action = args.getOptionValues("action").get(0);
        List<String> notices = args.getOptionValues("notices");

        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(4);
        threadPoolTaskScheduler.initialize();

        if ("cointwitter".equals(action)) {

        } else {
            log.error("action is not match");
        }
    }
}
