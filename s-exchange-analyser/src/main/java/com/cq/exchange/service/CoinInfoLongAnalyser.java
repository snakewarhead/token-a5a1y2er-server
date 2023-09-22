package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
import com.cq.exchange.entity.ExchangeAggTrade;
import com.cq.exchange.entity.ExchangeCoinInfo;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.cq.util.MathUtil;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CoinInfoLongAnalyser implements Runnable {

    private final ServiceContext serviceContext;

    private final ExchangeEnum exchangeEnum;
    private final ExchangeTradeType tradeType;
    private final String symbol;

    private ExchangePeriodEnum periodEnum = ExchangePeriodEnum.h4;

    private final static int RECENT_SIZE_AGGTRADE = 100_000;

    public CoinInfoLongAnalyser init() {

        return this;
    }

    public static String cron() {
        return "0 5 0/4 * * ?";
    }

    @Override
    public void run() {
        List<ExchangeAggTrade> trades = serviceContext.getExchangeAggTradeService().findRecently(exchangeEnum.getCode(), tradeType.getCode(), symbol, RECENT_SIZE_AGGTRADE);
        if (CollUtil.isEmpty(trades)) {
            return;
        }

        ExchangeCoinInfo info = new ExchangeCoinInfo();
        info.setExchangeId(exchangeEnum.getCode());
        info.setTradeType(tradeType.getCode());
        info.setSymbol(symbol);
        info.setPeriod(periodEnum.getSymbol());

        BigDecimal qtyMaxTradeBuy = BigDecimal.ZERO;
        BigDecimal qtyMaxTradeSell = BigDecimal.ZERO;

        BigDecimal priceAvgTrade = BigDecimal.ZERO;
        BigDecimal qtyAvgTradeBuy = BigDecimal.ZERO;
        BigDecimal qtyAvgTradeSell = BigDecimal.ZERO;
        BigDecimal qtyStdevTradeSell = BigDecimal.ZERO;
        BigDecimal qtyStdevTradeBuy = BigDecimal.ZERO;

        for (ExchangeAggTrade e : trades) {
            if (e.getBuyerMaker()) {
                qtyMaxTradeBuy = MathUtil.max(qtyAvgTradeBuy, BigDecimal.valueOf(e.getQuantity()));
            } else {
                qtyMaxTradeSell = MathUtil.max(qtyAvgTradeSell, BigDecimal.valueOf(e.getQuantity()));
            }
        }

        serviceContext.getExchangeCoinInfoService().save(info);
    }
}
