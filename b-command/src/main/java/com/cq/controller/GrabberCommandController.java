package com.cq.controller;

import com.cq.core.config.MqConfigCommand;
import com.cq.exchange.vo.ExchangeRunningParam;
import com.cq.vo.JSONResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/grabber")
public class GrabberCommandController {

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("forceOrderBinance")
    public JSONResult<?> forceOrderBinance(String symbol) {
        ExchangeRunningParam p = new ExchangeRunningParam();
        p.setType(2);
        rabbitTemplate.convertAndSend(MqConfigCommand.EXCHANGE_NAME, MqConfigCommand.ROUTING_KEY, p);
        return new JSONResult<>(true, 200, "", null);
    }
}
