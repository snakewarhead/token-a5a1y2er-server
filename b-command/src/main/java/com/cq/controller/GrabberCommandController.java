package com.cq.controller;

import com.cq.core.config.MqConfigCommand;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import com.cq.exchange.vo.ExchangeRunningParam;
import com.cq.exchange.vo.ExchangeRunningParamMSG;
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

    @GetMapping("forceOrder")
    public JSONResult<?> forceOrder(int exchange, int tradeType, String symbol, int subscribe) {
        if (!ExchangeEnum.contains(exchange)) {
            return JSONResult.error(400, "params error 1");
        }
        if (!ExchangeTradeType.contains(tradeType)) {
            return JSONResult.error(400, "params error 2");
        }

        ExchangeRunningParam p = new ExchangeRunningParam(exchange, tradeType);
        p.setAction(ExchangeRunningParam.ActionType.ForceOrder, symbol, null);
        ExchangeRunningParamMSG msg = new ExchangeRunningParamMSG(subscribe, p);

        rabbitTemplate.convertAndSend(MqConfigCommand.EXCHANGE_NAME, mapRoutingKey(exchange), msg);

        return JSONResult.success("");
    }

    private String mapRoutingKey(int exchange) {
        if (ExchangeEnum.BINANCE.is(exchange)) {
            return MqConfigCommand.ROUTING_KEY_BINANCE;
        }

        throw new IllegalStateException("Unexpected value: " + exchange);
    }
}
