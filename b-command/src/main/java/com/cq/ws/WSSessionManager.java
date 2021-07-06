package com.cq.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WSSessionManager {

    private ConcurrentHashMap<String, WebSocketSession> pool = new ConcurrentHashMap<>();

    public void add(String key, WebSocketSession session) {
        pool.put(key, session);
    }

    public WebSocketSession remove(String key) {
        return pool.remove(key);
    }

    public void removeAndClose(String key) {
        WebSocketSession session = remove(key);
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public WebSocketSession get(String key) {
        return pool.get(key);
    }


}