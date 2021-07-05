package com.cq.exchange.task;

import cn.hutool.core.thread.ThreadUtil;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.service.ExchangeRunningService;
import com.cq.exchange.vo.ExchangeRunningParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created by lin on 2020-11-06.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExchangeInitial implements ApplicationRunner {

    private final ExchangeRunningService exchangeRunningService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        int poolSize = 8;
        try {
            poolSize = Integer.parseInt(args.getOptionValues("threadPoolSize").get(0));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        exchangeRunningService.init(poolSize);

        ExchangeRunningParam p = null;
        try {
            String params = args.getOptionValues("params").get(0);
            p = ExchangeRunningParam.parse(params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (p == null) {
            throw new RuntimeException("must Set cmd line params");
        }
        p.setExchange(ExchangeEnum.BINANCE.getCode());

        exchangeRunningService.start(p);

        ThreadUtil.waitForDie(Thread.currentThread());
    }
}
