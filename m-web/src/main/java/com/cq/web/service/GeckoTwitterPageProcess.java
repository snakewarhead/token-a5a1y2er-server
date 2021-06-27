package com.cq.web.service;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.HtmlNode;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;

@Slf4j
public class GeckoTwitterPageProcess implements PageProcessor {

    public final static String HOST = "https://www.coingecko.com/en/coins/";

    private Site site = Site.me().setCycleRetryTimes(3).setRetrySleepTime(1000 * 10).setTimeOut(1000 * 10);

    @Override
    public void process(Page page) {
        boolean exist = false;
        try {
            do {
                PlainText pt = (PlainText) page.getHtml().xpath("//a[contains(@href, 'twitter.com') and @rel='nofollow noopener']/@href");
                if (pt == null) {
                    break;
                }
                String url = pt.getFirstSourceText();
                if (StrUtil.isEmpty(url)) {
                    break;
                }

                exist = true;
                page.putField("url", url);
            } while (false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        page.getResultItems().setSkip(!exist);

        page.putField("id", page.getRequest().getExtra("id"));
    }

    @Override
    public Site getSite() {
        return site;
    }
}
