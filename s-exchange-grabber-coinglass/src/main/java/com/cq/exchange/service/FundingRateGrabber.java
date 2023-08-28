package com.cq.exchange.service;

import cn.hutool.core.util.StrUtil;
import com.cq.exchange.vo.fundingrate.FundingRateCoinGlass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@RequiredArgsConstructor
public class FundingRateGrabber implements Runnable {

    private final static String path = "public/v2/funding";

    private final ServiceContext serviceContext;
    private final Config config;

    private OkHttpClient httpClient;
    private String url;
    private Headers headers;

    public static String cron(String periodStr) {
        return StrUtil.format("0 0/{} * * * ?", periodStr);
    }

    public Runnable init() {
        httpClient = new OkHttpClient.Builder().build();
        url = config.getUrl() + path;

        headers = new Headers.Builder()
            .add("accept", "application/json")
            .add("coinglassSecret", config.getApiSecret())
            .build();

        return this;
    }

    @Override
    public void run() {
        try {
            Request req = new Request.Builder()
                .url(url)
                .get()
                .headers(headers)
                .build();

            try (Response resp = httpClient.newCall(req).execute()) {
                if (!resp.isSuccessful()) {
                    log.error(resp.toString());
                    return;
                }

                FundingRateCoinGlass info = serviceContext.getJsonMapper().readValue(resp.body().string(), FundingRateCoinGlass.class);
                if (info == null) {
                    log.error("FundingRateCoinGlass parse error");
                    return;
                }
                if (!info.isSuccess()) {
                    log.error("FundingRateCoinGlass get failed");
                    return;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
