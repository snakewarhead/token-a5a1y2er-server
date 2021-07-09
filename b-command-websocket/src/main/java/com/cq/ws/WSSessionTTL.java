package com.cq.ws;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;

@Data
public class WSSessionTTL {

    public static final long TTL = 15 * 60 * 1000L;

    private WebSocketSession session;
    private long time;

    public WSSessionTTL(WebSocketSession session) {
        this.session = session;

        update();
    }

    public void update() {
        this.time = new Date().getTime();
    }

    public boolean isStale() {
        return new Date().getTime() - time > TTL;
    }
}
