package com.cq.exchange.receive;

import com.cq.core.config.MqConfigCommand;
import com.cq.exchange.vo.ExchangeRunningParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@RabbitListener(queues = MqConfigCommand.QUEUE_NAME)
public class ExchangeReceiver {

    @RabbitHandler
    public void process(ExchangeRunningParam param) {
       log.info(param.toString());
    }

}
