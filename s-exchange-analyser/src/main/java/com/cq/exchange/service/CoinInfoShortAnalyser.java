package com.cq.exchange.service;

import cn.hutool.core.map.FixedLinkedHashMap;
import cn.hutool.core.util.StrUtil;
import com.cq.exchange.entity.ExchangeCoinInfo;
import com.cq.exchange.entity.ExchangeCoinInfoRaw;
import com.cq.exchange.entity.ExchangeKline;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.enums.ExchangePeriodEnum;
import com.cq.exchange.enums.ExchangeTradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CoinInfoShortAnalyser implements Runnable {

    private final ServiceContext serviceContext;
    private final ExchangeEnum exchangeEnum;
    private final ExchangeTradeType tradeType;
    private final ExchangePeriodEnum periodEnum;

    private final static int LIMIT_KLINES = 1000;
    private Map<String, KlineInfo> mapKlines = new HashMap<>();

    public CoinInfoShortAnalyser init() {
        return this;
    }

    public String cron() throws Exception {
        if ("m".equals(periodEnum.getUnit())) {
            return StrUtil.format("7 0/{} * * * ?", periodEnum.getNum());
        }
        throw new Exception(StrUtil.format("period not support - {}", periodEnum.getSymbol()));
    }

    @Override
    public void run() {
        try {
            // all symbol trading
            List<ExchangeCoinInfoRaw> ls = serviceContext.getExchangeCoinInfoRawService().find(exchangeEnum.getCode(), tradeType.getCode(), 1);
            for (ExchangeCoinInfoRaw info : ls) {
                try {
                    DescriptiveStatistics stats = mapStatistics.get(info.getSymbol());
                    int limit = stats == null ? LIMIT_KLINES : 1;

                    List<ExchangeKline> klines = serviceContext.getExchangeKlineService().findLast(
                            exchangeEnum.getCode(),
                            tradeType.getCode(),
                            info.getSymbol(),
                            periodEnum.getSymbol(),
                            limit
                    ).toList();
                    double[] volumes = klines.stream().mapToDouble(i -> NumberUtils.toDouble(i.getVolume().toPlainString())).toArray();

                    if (stats == null) {
                        stats = new DescriptiveStatistics(volumes);
                        stats.setWindowSize(LIMIT_KLINES);
                    } else {
                        // XXX: filter the news
                        for (double v : volumes) {
                            stats.addValue(v);
                        }
                    }

                    // average volume of klines which will be trimmed with 4X stddev of up and down
                    double mean = stats.getMean();
                    double stdev = stats.getStandardDeviation();

                    ExchangeCoinInfo infoRipe = ExchangeCoinInfo.builder()
                            .period(periodEnum.getSymbol())
                            .qtyStdevVolume(NumberUtils.createBigDecimal(stdev + ""))
                            .qtyAvgSmoothVolume()
                            .build();
                    infoRipe.setExchangeId(exchangeEnum.getCode());
                    infoRipe.setTradeType(tradeType.getCode());
                    infoRipe.setSymbol(info.getSymbol());
                    infoRipe.setPair(info.getPair());
                    serviceContext.getExchangeCoinInfoService().save(infoRipe);

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private final class KlineInfo {
        FixedLinkedHashMap<String, ExchangeKline> klines = new FixedLinkedHashMap(LIMIT_KLINES);
        double[] volumes;
        double[] volumeQuotes;
    }
}
