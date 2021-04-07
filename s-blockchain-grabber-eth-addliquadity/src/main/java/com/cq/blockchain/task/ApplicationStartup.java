package com.cq.blockchain.task;

import com.cq.blockchain.config.SwapConfig;
import com.cq.blockchain.service.EthWeb3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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

        List<String> notices = args.getOptionValues("notices");
//        ethWeb3Service.grabberEventAddLiquidity(swapConfig.getContractAddressRouter(), swapConfig.getContractAddressFactory(), swapConfig.getLiquidityLimites());
        ethWeb3Service.scanNewPair(swapConfig.getContractAddressFactory(), swapConfig.getLiquidityLimites(), notices);
    }
}
