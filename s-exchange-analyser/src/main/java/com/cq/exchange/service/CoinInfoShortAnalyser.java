package com.cq.exchange.service;

import cn.hutool.core.collection.CollUtil;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CoinInfoShortAnalyser implements Runnable {

    private final ServiceContext serviceContext;
    private final ExchangeEnum exchangeEnum;
    private final ExchangeTradeType tradeType;
    private final ExchangePeriodEnum periodEnum;

    private final static int COUNT_RETRY = 2;
    private final static int LIMIT_KLINES = 1000;
    private final static int LIMIT_KLINES_MIN = 10;
    private Map<String, LinkedList<ExchangeKline>> symbolKlines = new HashMap<>();

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
            List<ExchangeCoinInfoRaw> ls = serviceContext.getExchangeCoinInfoRawService().find(exchangeEnum.getCode(), tradeType.getCode(), 1);
            for (ExchangeCoinInfoRaw info : ls) {
                try {
                    // continuous klines
                    boolean updated = true;
                    int count = COUNT_RETRY;
                    LinkedList<ExchangeKline> ksCached;
                    while (count-- > 0) {
                        ksCached = symbolKlines.get(info.getSymbol());
                        boolean nocached = CollUtil.isEmpty(ksCached);
                        List<ExchangeKline> klines = serviceContext.getExchangeKlineService().findOlder(
                                exchangeEnum.getCode(),
                                tradeType.getCode(),
                                info.getSymbol(),
                                periodEnum.getSymbol(),
                                nocached ? LIMIT_KLINES : LIMIT_KLINES_MIN
                        );
                        if (CollUtil.isEmpty(klines)) {
                            log.error("klines is empty. {}", info.getSymbol());
                            break;
                        }

                        if (nocached) {
                            ksCached = new LinkedList<>(klines);
                            symbolKlines.put(info.getSymbol(), ksCached);
                            break;
                        } else {
                            // is continuous?
                            ExchangeKline kc = ksCached.peekLast();
                            ExchangeKline kn = klines.get(0);
                            if (kn.getOpenTime() > serviceContext.getExchangeKlineService().nextPeriod(kc)) {
                                // this is not continuous, so retrieve all again
                                symbolKlines.remove(info.getSymbol());
                                log.warn("klines is not continuous. {}", info.getSymbol());
                                continue;
                            }

                            // has updated?
                            klines = klines.stream().filter(i -> i.getOpenTime() > kc.getOpenTime()).collect(Collectors.toList());
                            if (CollUtil.isEmpty(klines)) {
                                // there is nothing to update
                                updated = false;
                                break;
                            }

                            // fill the news

                        }
                    }
                    if (count <= 0) {
                        log.error("retry fail. {}", info.getSymbol());
                        continue;
                    }
                    if (CollUtil.isEmpty(ksCached)) {
                        log.error("ksCached is empty. {}", info.getSymbol());
                        continue;
                    }

                    // updating the coin info
                    if (!updated) {
                        continue;
                    }

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
}
