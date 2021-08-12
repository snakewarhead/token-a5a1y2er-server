package com.cq.web.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.MD5;
import com.cq.web.entity.CoinNews;
import com.cq.web.enums.CoinNewsChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class BinanceAnnouncementPipeline implements Pipeline {

    private final ServiceContext serviceContext;
    private final List<String> notices;

    private final static CoinNewsChannel channel = CoinNewsChannel.BINANCE_ANNOUCEMENT;
    private final MD5 md5 = MD5.create();

    @Override
    public void process(ResultItems resultItems, Task task) {
        try {
            List<String> res = resultItems.get("annoucements");
            List<CoinNews> all = res.stream().map(i -> {
                CoinNews c = new CoinNews();
                c.setChannel(channel.name());
                c.setTime(new Date());
                c.setContent(i);
                c.setParam0(md5.digestHex16(i));
                return c;
            }).collect(Collectors.toList());

            List<CoinNews> news = new ArrayList<>();
            CoinNews last = serviceContext.getCoinNewsService().findLast(channel);
            if (last == null) {
                news.addAll(all);
            } else {
                for (CoinNews c : all) {
                    String digest = md5.digestHex16(c.getContent());
                    if (digest.equals(last.getParam0())) {
                        break;
                    }
                    news.add(c);
                }
            }
            CollUtil.reverse(news);
            serviceContext.getCoinNewsService().saveAll(news);

            if (CollUtil.isNotEmpty(news)) {
                serviceContext.getMailService().sendMail(notices, channel.nameLowerCase(), news.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
