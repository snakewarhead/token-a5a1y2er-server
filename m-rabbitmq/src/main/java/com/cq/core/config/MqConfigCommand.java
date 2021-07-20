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

    public static final String QUEUE_NAME_ANALYSER = "queue_command_analyser";
    public static final String ROUTING_KEY_ANALYSER = "routing_key_command_analyser";

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
    Queue directQueueAnalyser() {
        return new Queue(QUEUE_NAME_ANALYSER, false);
    }

    @Bean
    Binding bindingDirectAnalyser(@Qualifier("directQueueAnalyser") Queue q, @Qualifier("directExchange") DirectExchange x) {
        return BindingBuilder.bind(q).to(x).with(ROUTING_KEY_ANALYSER);
    }
}
