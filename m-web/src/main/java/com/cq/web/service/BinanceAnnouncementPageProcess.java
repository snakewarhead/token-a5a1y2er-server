package com.cq.web.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.PlainText;

import java.util.List;
import java.util.stream.Collectors;

public class BinanceAnnouncementPageProcess implements PageProcessor {

    public final static String HOST = "https://www.binance.com/en/support/announcement/c-48?navId=48";

    private Site site = Site.me().setCycleRetryTimes(0).setRetrySleepTime(1000 * 5).setTimeOut(1000 * 5);

    @Override
    public void process(Page page) {
        PlainText pt = (PlainText) page.getHtml().xpath("//div[@class='css-6f91y1']/div[@class='css-vurnku']/a/text()");
        if (pt == null) {
            return;
        }
        List<String> ls = pt.nodes().stream().map(i -> ((PlainText) i).getFirstSourceText()).collect(Collectors.toList());
        if (CollUtil.isEmpty(ls)) {
            return;
        }
        ls = ls.stream().filter(StrUtil::isNotEmpty).collect(Collectors.toList());
        page.putField("annoucements", ls);
    }

    @Override
    public Site getSite() {
        return site;
    }
}
