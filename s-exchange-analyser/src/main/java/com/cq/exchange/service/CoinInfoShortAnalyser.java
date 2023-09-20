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
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import java.math.BigDecimal;
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
    private final static int MULTIPLE_STDEV = 4;    // 标准差的倍数，在这个倍数之外的数据认为是意外的

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
                    LinkedList<ExchangeKline> ksCached = null;
                    while (count-- > 0) {
                        ksCached = symbolKlines.get(info.getSymbol());
                        boolean nocached = CollUtil.isEmpty(ksCached);
                        List<ExchangeKline> klines = serviceContext.getExchangeKlineService().findOlder(
                                exchangeEnum.getCode(),
                                tradeType.getCode(),
                                info.getSymbol(),
                                periodEnum.getSymbol(),
                                1,
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
                            LinkedList<ExchangeKline> finalKsCached = ksCached;
                            klines.forEach(i -> {
                                // fixed size queue
                                if (finalKsCached.size() == LIMIT_KLINES) {
                                    finalKsCached.poll();
                                }
                                finalKsCached.offer(i);
                            });
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

                    // qtyStdevVolume
                    double[] volumes = ksCached.stream().mapToDouble(i -> i.getVolume().doubleValue()).toArray();
                    double qtyAvgVolume = StatUtils.mean(volumes);
                    double qtyStdevVolume = FastMath.sqrt(StatUtils.variance(volumes));
                    double qtyStdevVolumeRate = qtyStdevVolume / qtyAvgVolume;
                    double qtyAvgVolumeHigh = qtyAvgVolume + qtyStdevVolume * MULTIPLE_STDEV;
                    double qtyAvgVolumeLow = FastMath.max(qtyAvgVolume - qtyStdevVolume * MULTIPLE_STDEV, 0);

                    // filter klines to smooth - average volume of klines which will be trimmed with 4X stddev of up and down
                    List<ExchangeKline> ksCachedSmooth = ksCached.stream().filter(i -> i.getVolume().doubleValue() < qtyAvgVolumeHigh && i.getVolume().doubleValue() > qtyAvgVolumeLow).collect(Collectors.toList());
                    if (CollUtil.isEmpty(ksCachedSmooth)) {
                        continue;
                    }

                    double[] volumeSmooths = new double[ksCachedSmooth.size()];
                    double[] volumeQuotes = new double[ksCachedSmooth.size()];
                    double[] priceVolatilityRates = new double[ksCachedSmooth.size()];
                    for (int i = 0; i < ksCachedSmooth.size(); ++i) {
                        ExchangeKline k = ksCachedSmooth.get(i);
                        volumeSmooths[i] = k.getVolume().doubleValue();
                        volumeQuotes[i] = k.getQuoteVolume().doubleValue();

                        // price volatility rate: |H - L| / O
                        priceVolatilityRates[i] = FastMath.abs(k.getHigh().doubleValue() - k.getLow().doubleValue()) / k.getOpen().doubleValue();
                    }

                    // qtyAvgSmoothVolume
                    double qtyAvgSmoothVolume = StatUtils.mean(volumeSmooths);

                    // qtyAvgVolumeQuote
                    double qtyAvgVolumeQuote = StatUtils.mean(volumeQuotes);

                    // qtyAvgPriceVolatilityRate
                    double qtyAvgPriceVolatilityRate = StatUtils.mean(priceVolatilityRates);

                    ExchangeCoinInfo infoRipe = ExchangeCoinInfo.builder()
                            .period(periodEnum.getSymbol())
                            .qtyStdevVolume(BigDecimal.valueOf(qtyStdevVolume))
                            .qtyStdevVolumeRate(BigDecimal.valueOf(qtyStdevVolumeRate))
                            .qtyAvgSmoothVolume(BigDecimal.valueOf(qtyAvgSmoothVolume))
                            .qtyAvgVolumeQuote(BigDecimal.valueOf(qtyAvgVolumeQuote))
                            .qtyAvgPriceVolatilityRate(BigDecimal.valueOf(qtyAvgPriceVolatilityRate))
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
