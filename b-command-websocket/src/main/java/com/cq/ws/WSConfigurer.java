package com.cq.ws;

import com.cq.exchange.enums.ExchangeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WSConfigurer implements WebSocketConfigurer {

    private final ExchangeHandshakeInterceptor exchangeHandshakeInterceptor;
    private final ExchangeTextWebSocketHandler exchangeTextWebSocketHandler;

    @Value("${exchange}")
    private String exchange;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        ExchangeEnum e = ExchangeEnum.getEnum(exchange);
        if (e == null) {
            throw new RuntimeException("exchange not found");
        }
        registry.addHandler(exchangeTextWebSocketHandler, "api/v1/exchange/data/" + e.nameLowerCase())
                .addInterceptors(exchangeHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
