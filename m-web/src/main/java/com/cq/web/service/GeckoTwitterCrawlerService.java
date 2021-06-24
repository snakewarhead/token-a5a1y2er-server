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
import us.codecraft.webmagic.Spider;

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

    private final static String HOST = "https://www.coingecko.com/en/coins/";
    private final static String HOST_API = "https://api.coingecko.com/";
    private final static String URL_MARKETS = HOST_API + "api/v3/coins/markets";

    private List<CoinInfo> grabRank(int coinCategory, int numRank) {
        // 获取排行
        // https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&category=decentralized-finance-defi&order=market_cap_desc&per_page=100&page=1
        String url = URL_MARKETS + StrUtil.format(
                "?vs_currency={}&category={}&order={}&per_page={}&page={}",
                "usd",
                GeckoCoinCategory.map(coinCategory),
                "market_cap_desc",
                numRank,
                1
        );

        List<CoinInfo> ls = null;
        int retry = 0;
        while (retry++ < 3) {
            try {
                String res = HttpUtil.get(url, 1000 * 10);
                if (StrUtil.isEmpty(res)) {
                    ThreadUtil.sleep(1000 * 60 * 10);
                    continue;
                }
                ls = JSON.parseArray(res, CoinInfo.class);

                break;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (CollUtil.isEmpty(ls)) {
            throw new RuntimeException("Maybe gecko api changed");
        }

        ls.stream().forEach(i -> coinInfoDAO.save(i));

        return ls;
    }

    public void start(int coinCategory, int numRank, int numThread) {
        List<CoinInfo> ls = grabRank(coinCategory, numRank);
        List<String> urls = ls.stream().map(i -> HOST + i.getId()).collect(Collectors.toList());

        // 抓取twitter url
        Spider.create(new GeckoTwitterPageProcess())
                .addPipeline(new GeckoTwitterPipeline())
                .startUrls(urls)
                .thread(numThread)
                .start();
    }
}
