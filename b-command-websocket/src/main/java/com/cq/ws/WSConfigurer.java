package com.cq.ws;

import lombok.RequiredArgsConstructor;
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

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(exchangeTextWebSocketHandler, "api/v1/exchange/data")
                .addInterceptors(exchangeHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
