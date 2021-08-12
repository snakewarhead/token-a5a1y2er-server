package com.cq.web.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.web.dao.CoinNewsDAO;
import com.cq.web.entity.CoinNews;
import com.cq.web.enums.CoinNewsChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinNewsService {

    private final CoinNewsDAO coinNewsDAO;

    public CoinNews findLast(CoinNewsChannel channel) {
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "_id");
        Page<CoinNews> p = coinNewsDAO.findByChannel(channel.name(), pageable);
        List<CoinNews> ls = p.getContent();
        if (CollUtil.isEmpty(ls)) {
            return null;
        }
        return ls.get(0);
    }

    public void saveAll(List<CoinNews> ls) {
        coinNewsDAO.saveAll(ls);
    }
}
