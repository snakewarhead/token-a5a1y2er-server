package com.cq.controller;

import com.cq.core.config.MqConfigCommand;
import com.cq.exchange.enums.ExchangeActionType;
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
@RequestMapping("api/v1/exchange/command")
public class ExchangeCommandController {

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("action")
    public JSONResult<?> action(int exchange, int tradeType, String action, String symbol, String param, String subscribe) {
        if (!ExchangeEnum.contains(exchange)) {
            return JSONResult.error(400, "params error 1");
        }
        if (!ExchangeTradeType.contains(tradeType)) {
            return JSONResult.error(400, "params error 2");
        }
        if (!ExchangeActionType.contains(action)) {
            return JSONResult.error(400, "params error 3");
        }

        ExchangeRunningParam p = new ExchangeRunningParam(exchange, tradeType);
        p.setAction(ExchangeActionType.getEnum(action), symbol, param);
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
