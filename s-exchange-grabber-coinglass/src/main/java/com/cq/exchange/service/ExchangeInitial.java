package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.cq.exchange.service.ExchangeRunningService;
import com.cq.exchange.vo.ExchangeRunningParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExchangeInitial implements ApplicationRunner {

    private final ExchangeRunningService exchangeRunningService;

    @Override
    public void run(ApplicationArguments args) {
        int poolSize = 8;
        try {
            poolSize = Integer.parseInt(args.getOptionValues("threadPoolSize").get(0));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        exchangeRunningService.init(poolSize);

        try {
            List<ExchangeRunningParam> ps = new ArrayList<>();
            List<String> params = args.getOptionValues("params");
            for (String p : params) {
                ExchangeRunningParam pp = ExchangeRunningParam.parse(p);
                ps.add(pp);
            }
            if (CollUtil.isNotEmpty(ps)) {
                for (ExchangeRunningParam p : ps) {
                    exchangeRunningService.start(p, true);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        ThreadUtil.waitForDie(Thread.currentThread());
    }
}
