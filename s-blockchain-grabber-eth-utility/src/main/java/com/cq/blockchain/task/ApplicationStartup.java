package com.cq.blockchain.task;

import cn.hutool.core.thread.ThreadUtil;
import com.cq.blockchain.config.SwapConfig;
import com.cq.blockchain.service.EthWeb3Service;
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

    private final EthWeb3Service ethWeb3Service;
    private final SwapConfig swapConfig;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        String action = args.getOptionValues("action").get(0);
        List<String> notices = args.getOptionValues("notices");

        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(4);
        threadPoolTaskScheduler.initialize();

        boolean isActioning = true;
        if ("whale".equals(action)) {

        } else {
            isActioning = false;
            log.error("action is not match");
        }

        while (isActioning) {
            ThreadUtil.sleep(1000);
        }
    }
}
