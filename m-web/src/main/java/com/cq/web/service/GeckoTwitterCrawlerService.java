package com.cq.web.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.cq.web.dao.CoinInfoDAO;
import com.cq.web.entity.CoinInfo;
import com.cq.web.enums.GeckoCoinCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lin on 2021-05-27.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeckoTwitterCrawlerService {

    private final CoinInfoDAO coinInfoDAO;

    private final static String HOST_API = "https://api.coingecko.com/";
    private final static String URL_MARKETS = HOST_API + "api/v3/coins/markets";
    private final static int MAX_PER_PAGE = 50;

    private List<CoinInfo> grabRank(int coinCategory, int numRank) {
        List<CoinInfo> coinInfos = new ArrayList<>();
        int page = 0;
        int pageTotal = numRank / MAX_PER_PAGE;
        while (true) {
            page++;
            if (page > pageTotal) {
                break;
            }

            // 获取排行
            // https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&category=decentralized-finance-defi&order=market_cap_desc&per_page=100&page=1
            String url = URL_MARKETS + StrUtil.format(
                    "?vs_currency={}&category={}&order={}&per_page={}&page={}",
                    "usd",
                    GeckoCoinCategory.map(coinCategory),
                    "market_cap_desc",
                    MAX_PER_PAGE,
                    page
            );

            int retry = 0;
            while (retry++ < 3) {
                try {
                    String res = HttpUtil.get(url, 1000 * 10);
                    if (StrUtil.isEmpty(res)) {
                        ThreadUtil.sleep(1000 * 60 * 10);
                        continue;
                    }
                    List<CoinInfo> ls = JSON.parseArray(res, CoinInfo.class);
                    if (CollUtil.isEmpty(ls)) {
                        throw new RuntimeException("Maybe gecko api changed");
                    }
                    coinInfos.addAll(ls);

                    break;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        if (CollUtil.isEmpty(coinInfos)) {
            log.warn("gecko api can't get");
            return null;
        }

        coinInfos = coinInfos.stream().map(i -> {
            CoinInfo ii = coinInfoDAO.findById(i.getId()).orElse(null);
            if (ii != null) {
                i.setTwitter_url(ii.getTwitter_url());
            }
            return coinInfoDAO.save(i);
        }).collect(Collectors.toList());

        return coinInfos;
    }

    public void start(int coinCategory, int numRank, int numThread) {
        List<CoinInfo> ls = grabRank(coinCategory, numRank);
        if (CollUtil.isEmpty(ls)) {
            return;
        }

        List<Request> requests = ls.stream()
                .filter(i -> StrUtil.isEmpty(i.getTwitter_url()))
                .map(i -> {
                    String u = GeckoTwitterPageProcess.HOST + i.getId();
                    Request r = new Request(u);
                    r.putExtra("id", i.getId());
                    return r;
                })
                .collect(Collectors.toList());

        // 抓取twitter url
        Spider.create(new GeckoTwitterPageProcess())
                .addPipeline(new GeckoTwitterPipeline(coinInfoDAO))
                .startRequest(requests)
                .thread(numThread)
                .start();
    }
}
