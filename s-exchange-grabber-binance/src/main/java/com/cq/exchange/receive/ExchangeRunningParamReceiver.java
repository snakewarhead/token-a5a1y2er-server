package com.cq.exchange.receive;

import com.cq.core.config.MqConfigCommand;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.service.ExchangeRunningService;
import com.cq.exchange.vo.ExchangeRunningParam;
import com.cq.exchange.vo.ExchangeRunningParamMSG;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@RabbitListener(queues = MqConfigCommand.QUEUE_NAME_BINANCE)
public class ExchangeRunningParamReceiver {

    private final ExchangeRunningService exchangeRunningService;

    @RabbitHandler
    public void process(ExchangeRunningParamMSG msg) {
        if (msg == null || msg.getParam() == null) {
            return;
        }

        ExchangeRunningParam p = msg.getParam();
        if (ExchangeEnum.BINANCE.isNot(p.getExchange())) {
            return;
        }

        if (msg.isSubscribe()) {
            exchangeRunningService.start(p, false);
        } else {
            exchangeRunningService.stop(p);
        }
    }

}
