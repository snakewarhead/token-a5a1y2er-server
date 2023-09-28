package com.cq.exchange;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
public class NotifyContext {
    private final long TIME_STALE;

    private ConcurrentHashMap<String, Long> stales = new ConcurrentHashMap<>();

    public boolean fresh(String s) {
        String hash = SecureUtil.md5(s);
        Long timeStale = stales.get(hash);
        Long timeCurr = DateUtil.current();
        boolean fresh = timeStale == null || timeStale.compareTo(timeCurr) < 0;
        if (fresh) {
            stales.put(hash, timeCurr + TIME_STALE);
        }
        return fresh;
    }

    public void clean() {
        Long timeCurr = DateUtil.current();
        stales.values().removeIf(v -> v.compareTo(timeCurr) < 0);
    }
}
