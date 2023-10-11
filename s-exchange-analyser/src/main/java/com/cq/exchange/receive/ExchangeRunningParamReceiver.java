package com.cq.exchange.receive;

import com.cq.core.config.MqConfigCommand;
import com.cq.exchange.service.ExchangeRunningService;
import com.cq.exchange.vo.ExchangeRunningParam;
import com.cq.exchange.vo.ExchangeRunningParamMSG;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@RabbitListener(
        bindings = {@QueueBinding(
                value = @Queue(name = MqConfigCommand.QUEUE_NAME_ANALYSER, durable = "false"),
                exchange = @Exchange(name = MqConfigCommand.EXCHANGE_NAME, durable = "false"),
                key = {MqConfigCommand.ROUTING_KEY_ANALYSER})})
public class ExchangeRunningParamReceiver {

    private final ExchangeRunningService exchangeRunningService;

    @RabbitHandler
    public void process(ExchangeRunningParamMSG msg) {
        try {
            if (msg == null || msg.getParam() == null) {
                return;
            }

            ExchangeRunningParam p = msg.getParam();
            if (msg.isSubscribe()) {
                exchangeRunningService.start(p, false);
            } else {
                exchangeRunningService.stop(p);
            }
        } catch (Exception e) {
            // XXX: consume msg
            log.error(e.getMessage(), e);
        }
    }

}
