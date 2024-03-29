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

        ExchangeActionType actionType = ExchangeActionType.getEnum(action);
        if (actionType == null) {
            return JSONResult.error(400, "params error 3");
        }

        ExchangeRunningParam p = new ExchangeRunningParam(exchange, tradeType);
        p.putAction(actionType, symbol, param);
        ExchangeRunningParamMSG msg = new ExchangeRunningParamMSG(subscribe, p);

        rabbitTemplate.convertAndSend(MqConfigCommand.EXCHANGE_NAME, mapRoutingKey(exchange, actionType), msg);

        return JSONResult.success("");
    }

    private String mapRoutingKey(int exchange, ExchangeActionType action) {
        if (ExchangeEnum.BINANCE.is(exchange)) {
            return action.isGrabber() ? MqConfigCommand.ROUTING_KEY_BINANCE_GRABBER : MqConfigCommand.ROUTING_KEY_ANALYSER;
        }

        throw new IllegalStateException("Unexpected value: " + exchange);
    }
}
