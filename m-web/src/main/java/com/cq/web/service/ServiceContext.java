package com.cq.web.service;

import com.cq.core.service.MailService;
import com.cq.web.dao.CoinInfoDAO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceContext {

    private final CoinInfoDAO coinInfoDAO;
    private final CoinNewsService coinNewsService;

    private final MailService mailService;

}
