package com.cq.web.service;

import com.cq.web.dao.CoinNewsDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterNewsCrawlerService {

    private final CoinNewsDAO coinNewsDAO;

    // "//section//div[contains(@style, 'translateY')]"
    public void start() {

    }

}
