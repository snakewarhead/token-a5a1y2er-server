package com.cq.blockchain.task;

import com.cq.blockchain.dao.ConfigEthDAO;
import com.cq.blockchain.entity.ConfigEth;
import com.cq.blockchain.service.EthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created by lin on 2020-09-23.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ApplicationStartup implements ApplicationRunner {

    @Value(value = "${wallet.ip}")
    private String ip;
    @Value(value = "${wallet.port}")
    private String port;
    @Value(value = "${wallet.password}")
    private String password;
    @Value(value = "${wallet.height}")
    private long height;

    private final EthService ethService;
    private final ConfigEthDAO configEthDAO;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // init config
        ConfigEth c = configEthDAO.findById(1).orElse(null);
        if (c == null) {
            c = new ConfigEth().setId(1).setCurrentBlockHeight(0L);
            configEthDAO.save(c);
        }

        while (true) {
            try {
                ethService.detect(ip, port, password, height);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            Thread.sleep(30 * 1000);
        }
    }
}
