package com.cq.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cq.web.entity.CoinInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

@Slf4j
public class GeckoTwitterCrawlerServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCoinInfo() {
        String res = "{\"id\":\"uniswap\",\"symbol\":\"uni\",\"name\":\"Uniswap\",\"image\":\"https://assets.coingecko.com/coins/images/12504/large/uniswap-uni.png?1600306604\",\"current_price\":17.91,\"market_cap\":9347251175,\"market_cap_rank\":11,\"fully_diluted_valuation\":17980414222,\"total_volume\":807803632,\"high_24h\":18.27,\"low_24h\":14.12,\"price_change_24h\":1.43,\"price_change_percentage_24h\":8.69282,\"market_cap_change_24h\":754089051,\"market_cap_change_percentage_24h\":8.77545,\"circulating_supply\":519857388.1320768,\"total_supply\":1000000000.0,\"max_supply\":1000000000.0,\"ath\":44.92,\"ath_change_percentage\":-60.06275,\"ath_date\":\"2021-05-03T05:25:04.822Z\",\"atl\":1.03,\"atl_change_percentage\":1641.3651,\"atl_date\":\"2020-09-17T01:20:38.214Z\",\"roi\":null,\"last_updated\":\"2021-06-23T07:59:27.297Z\"}";
        CoinInfo c = JSON.parseObject(res, CoinInfo.class);
        log.info(c.toString());

        res = "[{\"id\":\"uniswap\",\"symbol\":\"uni\",\"name\":\"Uniswap\",\"image\":\"https://assets.coingecko.com/coins/images/12504/large/uniswap-uni.png?1600306604\",\"current_price\":17.91,\"market_cap\":9347251175,\"market_cap_rank\":11,\"fully_diluted_valuation\":17980414222,\"total_volume\":807803632,\"high_24h\":18.27,\"low_24h\":14.12,\"price_change_24h\":1.43,\"price_change_percentage_24h\":8.69282,\"market_cap_change_24h\":754089051,\"market_cap_change_percentage_24h\":8.77545,\"circulating_supply\":519857388.1320768,\"total_supply\":1000000000.0,\"max_supply\":1000000000.0,\"ath\":44.92,\"ath_change_percentage\":-60.06275,\"ath_date\":\"2021-05-03T05:25:04.822Z\",\"atl\":1.03,\"atl_change_percentage\":1641.3651,\"atl_date\":\"2020-09-17T01:20:38.214Z\",\"roi\":null,\"last_updated\":\"2021-06-23T07:59:27.297Z\"},{\"id\":\"chainlink\",\"symbol\":\"link\",\"name\":\"Chainlink\",\"image\":\"https://assets.coingecko.com/coins/images/877/large/chainlink-new-logo.png?1547034700\",\"current_price\":18.85,\"market_cap\":8210741807,\"market_cap_rank\":15,\"fully_diluted_valuation\":18918343463,\"total_volume\":2365748600,\"high_24h\":19.0,\"low_24h\":15.15,\"price_change_24h\":0.85036,\"price_change_percentage_24h\":4.723,\"market_cap_change_24h\":374220253,\"market_cap_change_percentage_24h\":4.77534,\"circulating_supply\":434009553.9174637,\"total_supply\":1000000000.0,\"max_supply\":1000000000.0,\"ath\":52.7,\"ath_change_percentage\":-64.18156,\"ath_date\":\"2021-05-10T00:13:57.214Z\",\"atl\":0.148183,\"atl_change_percentage\":12637.67885,\"atl_date\":\"2017-11-29T00:00:00.000Z\",\"roi\":null,\"last_updated\":\"2021-06-23T07:59:33.281Z\"}]";
        List<CoinInfo> ls = JSON.parseArray(res, CoinInfo.class);

        log.info(ls.toString());
    }
}