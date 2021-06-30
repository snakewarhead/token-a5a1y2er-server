package com.cq.web.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.web.dao.CoinInfoDAO;
import com.cq.web.dao.CoinNewsDAO;
import com.cq.web.entity.CoinInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterNewsCrawlerService {

    private final CoinInfoDAO coinInfoDAO;
    private final CoinNewsDAO coinNewsDAO;

    private Spider spider;

    @PostConstruct
    public void init() {
        spider = new Spider(new TwitterNewsPageProcess()).addPipeline(new TwitterNewsPipeline());
    }

    // "//section//div[contains(@style, 'translateY')]"
    public void start(List<String> twittersExtend, int numRank, List<String> notices, int numThread) {
        if (spider.getStatus() == Spider.Status.Running) {
            log.warn("spider is busy!!!!!!!!");
            return;
        }

        twittersExtend = twittersExtend.stream().map(i -> TwitterNewsPageProcess.HOST + i).collect(Collectors.toList());

        // combine
        List<CoinInfo> coins = findAllInMarketCap(numRank);
        List<String> twitters = coins.stream().map(i -> TwitterNewsPageProcess.HOST + i.getId()).collect(Collectors.toList());
        twitters.addAll(twittersExtend);
        if (CollUtil.isEmpty(twitters)) {
            log.warn("There are no twitters to need to crawl!!!!!!!!");
            return;
        }
    }

    public List<CoinInfo> findAllInMarketCap(int numRank) {
        Pageable p = PageRequest.of(
                0,
                numRank,
                Sort.by(Sort.Direction.DESC, "market_cap")
        );

        Page<CoinInfo> r = coinInfoDAO.findAll(p);
        return r.getContent();
    }
}
