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
    public static final String QUEUE_NAME = "queue_command";
    public static final String ROUTING_KEY = "routing_key_command";

    @Bean
    Queue directQueue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding bindingDirect(@Qualifier("directQueue") Queue q, @Qualifier("directExchange") DirectExchange x) {
        return BindingBuilder.bind(q).to(x).with(ROUTING_KEY);
    }

}
