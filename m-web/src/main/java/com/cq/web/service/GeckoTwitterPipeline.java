package com.cq.web.service;

import cn.hutool.core.util.StrUtil;
import com.cq.web.dao.CoinInfoDAO;
import com.cq.web.entity.CoinInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Slf4j
@RequiredArgsConstructor
public class GeckoTwitterPipeline implements Pipeline {

    private final CoinInfoDAO coinInfoDAO;

    @Override
    public void process(ResultItems resultItems, Task task) {
        String id = resultItems.get("id");
        if (StrUtil.isEmpty(id)) {
            log.error("id is empty");
            return;
        }

        String url = resultItems.get("url");
        if (StrUtil.isEmpty(url)) {
            log.error("url is empty");
            return;
        }

        CoinInfo info = coinInfoDAO.findById(id).orElse(null);
        info.setTwitter_url(url);
        coinInfoDAO.save(info);
    }
}
