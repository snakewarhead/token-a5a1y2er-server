package com.cq.web.service;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Slf4j
public class GeckoTwitterPageProcess implements PageProcessor {

    private Site site = Site.me().setCycleRetryTimes(3).setRetrySleepTime(1000 * 60).setTimeOut(1000 * 10);

    @Override
    public void process(Page page) {
        log.info(page.toString());
    }

    @Override
    public Site getSite() {
        return site;
    }
}
