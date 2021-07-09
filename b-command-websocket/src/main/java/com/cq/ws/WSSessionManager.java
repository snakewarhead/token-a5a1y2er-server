package com.cq.ws;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
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

    @Scheduled(fixedDelay = 1000 * 60)
    public void cleanStale() {
        long now = DateUtil.date().getTime();
        for (Map.Entry<String, WSSessionTTL> e : pool.entrySet()) {
            if (now - e.getValue().getTime() > WSSessionTTL.TTL) {
                removeAndClose(e.getKey());
            }
        }
    }

}