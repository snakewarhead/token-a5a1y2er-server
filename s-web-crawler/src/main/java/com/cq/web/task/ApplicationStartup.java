package com.cq.web.task;

import cn.hutool.core.thread.ThreadUtil;
import com.cq.web.config.Config;
import com.cq.web.service.BinanceAnnouncementCrawlerService;
import com.cq.web.service.GeckoTwitterCrawlerService;
import com.cq.web.service.TwitterNewsCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Created by lin on 2020-09-23.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ApplicationStartup implements ApplicationRunner {

    private final GeckoTwitterCrawlerService geckoTwitterCrawlerService;
    private final TwitterNewsCrawlerService twitterNewsCrawlerService;
    private final BinanceAnnouncementCrawlerService binanceAnnouncementCrawlerService;
    private final Config config;

    private final static long DURATION_GECKO_TWITTER_CRAWL = 1000 * 60 * 60 * 12;
    private final static long DURATION_TWITTER_NEWS_CRAWL = 1000 * 60 * 15;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        int numThread = 4;
        try {
            numThread = Integer.parseInt(args.getOptionValues("thread").get(0));
        } catch (Exception e) {
        }
        String action = args.getOptionValues("action").get(0);
        List<String> params = args.getOptionValues("params");

        if ("gecko_twitter_crawl".equals(action)) {
            while (true) {
                int coinCategory = 1;
                try {
                    coinCategory = Integer.parseInt(params.get(0));
                } catch (Exception e) {
                }

                int numRank = 100;
                try {
                    numRank = Integer.parseInt(params.get(1));
                } catch (Exception e) {
                }
                // crawl from coin gecko
                geckoTwitterCrawlerService.start(coinCategory, numRank, numThread);

                ThreadUtil.sleep(DURATION_GECKO_TWITTER_CRAWL);
            }
        } else if ("twitter_news_crawl".equals(action)) {
            List<String> notices = config.getNotices();
            List<String> twittersInConfig = config.getTwitters();

            int numRank = 100;
            try {
                numRank = Integer.parseInt(params.get(0));
            } catch (Exception e) {
            }

            while (true) {
                twitterNewsCrawlerService.start(twittersInConfig, numRank, notices, numThread);

                ThreadUtil.sleep(DURATION_TWITTER_NEWS_CRAWL);
            }
        } else if ("binance_announcement_crawl".equals(action)) {
            int interval = 20000;
            try {
                interval = Integer.parseInt(params.get(0));
            } catch (Exception e) {
            }

            binanceAnnouncementCrawlerService.init(config.getNotices());
            while (true) {
                binanceAnnouncementCrawlerService.loop();
                ThreadUtil.sleep(interval);
            }
        } else {
            log.error("action is not match");
        }
    }
}
