package com.cq.core.service;


import cn.hutool.http.HttpUtil;
import com.cq.core.vo.MailMsg;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MailClient {

    private OkHttpClient httpClient;
    private Headers headers;

    @Value("${mail.remote.url}")
    private String url;
    @Value("${mail.custom.to}")
    private String to;

    @PostConstruct
    private void init() {
        httpClient = new OkHttpClient.Builder().build();

        headers = new Headers.Builder()
                .add("accept", "application/json")
                .build();
    }

    public void send(MailMsg msg) throws IOException {
        Map<String, String> mapParams = new HashMap<>();
        mapParams.put("data", msg.toJson());
        mapParams.put("emails", msg.getEmails() == null ? to : msg.getEmails());
        mapParams.put("silent", msg.getSilent() == null ? null : msg.getSilent().toJson());
        String strParams = HttpUtil.toParams(mapParams);
        Request req = new Request.Builder()
                .url(url + "?" + strParams)
                .get()
                .headers(headers)
                .build();

        try (Response resp = httpClient.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                log.error(resp.toString());
                return;
            }
            log.info("mail sended {}", resp.body().string());
        }
    }
}
