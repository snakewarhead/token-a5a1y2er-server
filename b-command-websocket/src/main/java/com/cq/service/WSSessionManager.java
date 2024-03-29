package com.cq.service;

import com.cq.ws.WSSessionTTL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.PingMessage;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WSSessionManager {

    private ConcurrentHashMap<String, WSSessionTTL> pool = new ConcurrentHashMap<>();

    public void add(String key, WSSessionTTL session) {
        pool.put(key, session);
    }

    public WSSessionTTL remove(String key) {
        return pool.remove(key);
    }

    public void removeAndClose(String key) {
        WSSessionTTL s = remove(key);
        if (s != null) {
            if (s.getSession() != null) {
                try {
                    s.getSession().close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public WSSessionTTL get(String key) {
        return pool.get(key);
    }

    public void onFresh(String key) {
        WSSessionTTL s = get(key);
        if (s == null) {
            log.warn("session is null. {}", key);
            return;
        }
        s.update();
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void fresh() {
        for (Map.Entry<String, WSSessionTTL> entry : pool.entrySet()) {
            try {
                entry.getValue().getSession().sendMessage(new PingMessage());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void cleanStale() {
        for (Iterator<String> itor = pool.keySet().iterator(); itor.hasNext(); ) {
            String k = itor.next();
            WSSessionTTL v = pool.get(k);
            if (v.isStale()) {
                try {
                    removeAndClose(k);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}