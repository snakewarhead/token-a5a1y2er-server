package com.cq.ws;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class WSSessionTTL {

    public static final long TTL = 15 * 60 * 1000L;
    private WebSocketSession session;
    private long time;
}
