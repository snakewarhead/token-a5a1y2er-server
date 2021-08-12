package com.cq.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * Created by lin on 2021-05-27.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceAnnouncementCrawlerService {

    private final ServiceContext serviceContext;

    private Spider spider;

    public void init(List<String> notices) {
        spider = new Spider(new BinanceAnnouncementPageProcess()).addPipeline(new BinanceAnnouncementPipeline(serviceContext, notices)).thread(1);
        spider.start();
    }

    public void loop() {
        spider.addUrl(BinanceAnnouncementPageProcess.HOST);
    }
}
