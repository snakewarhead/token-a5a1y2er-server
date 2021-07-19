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

    public static final String QUEUE_NAME_BINANCE_GRABBER = "queue_command_binance_grabber";
    public static final String ROUTING_KEY_BINANCE_GRABBER = "routing_key_command_binance_grabber";

    public static final String QUEUE_NAME_BINANCE_ANALYSER = "queue_command_binance_analyser";
    public static final String ROUTING_KEY_BINANCE_ANALYSER = "routing_key_command_binance_analyser";

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Queue directQueueBinanceGrabber() {
        return new Queue(QUEUE_NAME_BINANCE_GRABBER, false);
    }

    @Bean
    Binding bindingDirectBinanceGrabber(@Qualifier("directQueueBinanceGrabber") Queue q, @Qualifier("directExchange") DirectExchange x) {
        return BindingBuilder.bind(q).to(x).with(ROUTING_KEY_BINANCE_GRABBER);
    }

    @Bean
    Queue directQueueBinanceAnalyser() {
        return new Queue(QUEUE_NAME_BINANCE_ANALYSER, false);
    }

    @Bean
    Binding bindingDirectBinanceAnalyser(@Qualifier("directQueueBinanceAnalyser") Queue q, @Qualifier("directExchange") DirectExchange x) {
        return BindingBuilder.bind(q).to(x).with(ROUTING_KEY_BINANCE_ANALYSER);
    }
}
