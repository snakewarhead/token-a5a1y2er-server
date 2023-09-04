package com.cq.exchange.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import com.cq.core.config.MqConfigCommand;
import com.cq.exchange.entity.ExchangeFutureFundingRate;
import com.cq.exchange.enums.ExchangeActionType;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import com.cq.exchange.vo.ExchangeRunningParam;
import com.cq.exchange.vo.ExchangeRunningParamMSG;
import com.cq.exchange.vo.fundingrate.FundingRateCoinGlass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FundingRateGrabber implements Runnable {

    private final static String path = "public/v2/funding";

    private final ServiceContext serviceContext;
    private final RabbitTemplate rabbitTemplate;
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
            long timeCurrent = DateUtil.current(false);
            StopWatch sw = new StopWatch("funding rate");

            Request req = new Request.Builder()
                .url(url)
                .get()
                .headers(headers)
                .build();

            sw.start("request");
            try (Response resp = httpClient.newCall(req).execute()) {
                sw.stop();
                if (!resp.isSuccessful()) {
                    log.error(resp.toString());
                    return;
                }

                sw.start("parse");
                FundingRateCoinGlass info = serviceContext.getJsonMapper().readValue(resp.body().string(), FundingRateCoinGlass.class);
                sw.stop();
                if (info == null) {
                    log.error("FundingRateCoinGlass parse error");
                    return;
                }
                if (!info.isSuccess()) {
                    log.error("FundingRateCoinGlass get failed");
                    return;
                }
                sw.start("adapt");
                List<ExchangeFutureFundingRate> ls = adapt(info, timeCurrent);
                sw.stop();

                // store in db
                sw.start("store");
                serviceContext.getExchangeFutureFundingRateSerivce().saveAll(ls);
                sw.stop();

                // send to analyze
                sw.start("mq");
                ExchangeRunningParamMSG msg = ExchangeRunningParamMSG.builder()
                    .subscribe(ExchangeRunningParamMSG.SUBSCRIBE)
                    .param(new ExchangeRunningParam().setAction(ExchangeActionType.FundingRateRank, null, null))
                    .build();
                rabbitTemplate.convertAndSend(MqConfigCommand.EXCHANGE_NAME, MqConfigCommand.ROUTING_KEY_ANALYSER, msg);
                sw.stop();

                log.info(sw.prettyPrint());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<ExchangeFutureFundingRate> adapt(FundingRateCoinGlass in, long timeCurrent) {
        List<ExchangeFutureFundingRate> ls = new ArrayList<>();

        in.getData().stream().forEach(i -> {
            if (i.getStatus() != 0) {
                return;
            }

            i.getUMarginList().stream().forEach(j -> {
                ExchangeEnum ee = ExchangeEnum.getEnum(j.getExchangeName());
                if (ee == null) {
                    log.warn("ExchangeEnum not match in UMargin. {}", j.getExchangeName());
                    return;
                }

                ExchangeFutureFundingRate r = new ExchangeFutureFundingRate();
                r.setExchangeId(ee.getCode());
                r.setSymbol(i.getSymbol());
                r.setTradeType(ExchangeTradeType.FUTURE_USDT.getCode());

                r.setIndexPrice(i.getUIndexPrice());
                r.setMarkPrice(i.getUPrice());
                r.setTime(timeCurrent);
                r.setNextFundingTime(j.getNextFundingTime());

                r.setLastFundingRate(j.getRate());
                r.setEstimatedRate(j.getPredictedRate());

                ls.add(r);
            });

            i.getCMarginList().stream().forEach(j -> {
                ExchangeEnum ee = ExchangeEnum.getEnum(j.getExchangeName());
                if (ee == null) {
                    log.warn("ExchangeEnum not match in CMargin. {}", j.getExchangeName());
                    return;
                }

                ExchangeFutureFundingRate r = new ExchangeFutureFundingRate();
                r.setExchangeId(ee.getCode());
                r.setSymbol(i.getSymbol());
                r.setTradeType(ExchangeTradeType.FUTURE_COIN.getCode());

                r.setIndexPrice(i.getCIndexPrice());
                r.setMarkPrice(i.getCPrice());
                r.setTime(timeCurrent);
                r.setNextFundingTime(j.getNextFundingTime());

                r.setLastFundingRate(j.getRate());
                r.setEstimatedRate(j.getPredictedRate());

                ls.add(r);
            });
        });

        return ls;
    }
}
