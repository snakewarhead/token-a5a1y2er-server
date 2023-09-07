package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.cq.core.vo.MailMsg;
import com.cq.exchange.entity.ExchangeFutureFundingRate;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class FundingRateRankAnalyser implements Runnable {

    private final ServiceContext serviceContext;

    private final long timeQuery;

    private final static long TIME_STALE = 24 * 3600 * 1000;
    private static ConcurrentHashMap<String, Long> stales = new ConcurrentHashMap<>();

    private final static BigDecimal rateLimit = BigDecimal.valueOf(0.1);

    private final static int NUM_RANK = 5;
    private final static int TYPE_TRADE = ExchangeTradeType.FUTURE_USDT.getCode();
    private final static int[] IDS_EXCHANGE = {
            ExchangeEnum.BINANCE.getCode(),
            ExchangeEnum.OKX.getCode(),
            ExchangeEnum.HUOBI.getCode(),
            ExchangeEnum.GATEIO.getCode(),
    };

    public FundingRateRankAnalyser init() {
        return this;
    }

    @Override
    public void run() {
        List<ExchangeFutureFundingRate> lsNegative = serviceContext.getExchangeFutureFundingRateSerivce().findInRateRank(IDS_EXCHANGE, TYPE_TRADE, timeQuery, NUM_RANK, 1);
        List<ExchangeFutureFundingRate> lsPositive = serviceContext.getExchangeFutureFundingRateSerivce().findInRateRank(IDS_EXCHANGE, TYPE_TRADE, timeQuery, NUM_RANK, -1);
        if (!needNotify(lsNegative) && !needNotify(lsPositive)) {
            return;
        }
        ExchangeFutureFundingRate rn = lsNegative.get(0);
        ExchangeFutureFundingRate rp = lsPositive.get(0);
        ExchangeFutureFundingRate rmore = rn.getLastFundingRate().abs().compareTo(rp.getLastFundingRate().abs()) > 0 ? rn : rp;

        // notify in html
        String t0 = htmlTable("负费率", lsNegative);
        String t1 = htmlTable("正费率", lsPositive);

        try {
            MailMsg msg = MailMsg.builder()
                    .subject("funding rate rank")
                    .text(t0 + t1)
                    .silent(MailMsg.Silent.builder()
                            .hash(hash(rmore))
                            .deadline(TIME_STALE)
                            .build())
                    .build();
            serviceContext.getMailClient().send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean needNotify(List<ExchangeFutureFundingRate> ls) {
        if (CollUtil.isEmpty(ls)) {
            return false;
        }

        ExchangeFutureFundingRate r = ls.get(0);
        boolean over = r.getLastFundingRate().abs().compareTo(rateLimit) >= 0;

        String hash = hash(r);
        Long timeStale = stales.get(hash);
        Long timeCurr = DateUtil.current(false);
        boolean fresh = timeStale == null || timeStale.compareTo(timeCurr) < 0;
        if (fresh) {
            stales.put(hash, timeCurr + TIME_STALE);
        }

        return over && fresh;
    }

    private String hash(ExchangeFutureFundingRate r) {
        return SecureUtil.md5(r.getSymbol());
//        return SecureUtil.md5(r.getExchangeId() + r.getSymbol() + r.getTradeType());
    }

    private String htmlTable(String tilte, List<ExchangeFutureFundingRate> ls) {
        // trade-type
        // symbol exchange rate price index-price estimated-rate
        List<String> headers = new ArrayList<>();
        headers.add("symbol");
        headers.add("exchange");
        headers.add("rate(%)");
        headers.add("price");
        headers.add("index-price");
        headers.add("estimated-rate(%)");

        List<List<String>> contents = new ArrayList<>();
        for (ExchangeFutureFundingRate r : ls) {
            List<String> ct = new ArrayList<>();

            ct.add(r.getSymbol());
            ct.add(ExchangeEnum.getEnum(r.getExchangeId()).name());
            ct.add(r.getLastFundingRate() != null ? r.getLastFundingRate().toPlainString() : "");
            ct.add(r.getMarkPrice() != null ? r.getMarkPrice().toPlainString() : "");
            ct.add(r.getIndexPrice() != null ? r.getIndexPrice().toPlainString() : "");
            ct.add(r.getEstimatedRate() != null ? r.getEstimatedRate().toPlainString() : "");

            contents.add(ct);
        }

        return serviceContext.getHtmlContentBuilder().table(tilte, headers, contents);
    }
}
