package com.cq.core.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfigCommand {

    public static final String EXCHANGE_NAME = "exchange_command";

    public static final String QUEUE_NAME_BINANCE_GRABBER = "queue_command_binance_grabber";
    public static final String ROUTING_KEY_BINANCE_GRABBER = "routing_key_command_binance_grabber";

    public static final String QUEUE_NAME_COINGLASS_GRABBER = "queue_command_coinglass_grabber";
    public static final String ROUTING_KEY_COINGLASS_GRABBER = "routing_key_command_coinglass_grabber";

    public static final String QUEUE_NAME_ANALYSER = "queue_command_analyser";
    public static final String ROUTING_KEY_ANALYSER = "routing_key_command_analyser";

//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate();
//        rabbitTemplate.setConnectionFactory(connectionFactory);
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//        return rabbitTemplate;
//    }
}
