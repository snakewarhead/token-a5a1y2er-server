package com.cq.web.service;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Slf4j
public class GeckoTwitterPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        log.info(resultItems.toString());
    }
}
