package com.cq.core.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfigCommand {

    public static final String EXCHANGE_NAME = "exchange_command";
    public static final String QUEUE_NAME_BINANCE = "queue_command_binance";
    public static final String ROUTING_KEY_BINANCE = "routing_key_command_binance";

    @Bean
    Queue directQueueBinance() {
        return new Queue(QUEUE_NAME_BINANCE, false);
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding bindingDirectBinance(@Qualifier("directQueueBinance") Queue q, @Qualifier("directExchange") DirectExchange x) {
        return BindingBuilder.bind(q).to(x).with(ROUTING_KEY_BINANCE);
    }

}
