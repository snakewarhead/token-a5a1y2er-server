package com.cq.web.service;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class TwitterNewsPageProcess implements PageProcessor {

    public final static String HOST = "https://twitter.com/";

    private Site site = Site.me().setCycleRetryTimes(0).setRetrySleepTime(1000 * 10).setTimeOut(1000 * 10);

    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return site;
    }
}
