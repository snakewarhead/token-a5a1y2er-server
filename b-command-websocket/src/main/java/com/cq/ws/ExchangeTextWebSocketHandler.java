package com.cq.ws;

import com.cq.service.WSSessionManager;
import com.cq.service.WSSessionPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExchangeTextWebSocketHandler extends TextWebSocketHandler {

    private final WSSessionManager wsSessionManager;
    private final WSSessionPublisher wsSessionPublisher;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        wsSessionManager.add(session.getId(), new WSSessionTTL(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        wsSessionManager.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WSSessionTTL ws = wsSessionManager.get(session.getId());
        if (ws == null) {
            log.warn("session ttl not found. {} - {}", session.toString(), message.getPayload());
            return;
        }

        wsSessionPublisher.receive(session, message.getPayload());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        wsSessionManager.onFresh(session.getId());
    }
}
