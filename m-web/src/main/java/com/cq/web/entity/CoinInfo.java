package com.cq.web.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Document(collection = "coin_info")
public class CoinInfo {

    // {"id":"uniswap","symbol":"uni","name":"Uniswap","image":"https://assets.coingecko.com/coins/images/12504/large/uniswap-uni.png?1600306604","current_price":17.91,"market_cap":9347251175,"market_cap_rank":11,"fully_diluted_valuation":17980414222,"total_volume":807803632,"high_24h":18.27,"low_24h":14.12,"price_change_24h":1.43,"price_change_percentage_24h":8.69282,"market_cap_change_24h":754089051,"market_cap_change_percentage_24h":8.77545,"circulating_supply":519857388.1320768,"total_supply":1000000000.0,"max_supply":1000000000.0,"ath":44.92,"ath_change_percentage":-60.06275,"ath_date":"2021-05-03T05:25:04.822Z","atl":1.03,"atl_change_percentage":1641.3651,"atl_date":"2020-09-17T01:20:38.214Z","roi":null,"last_updated":"2021-06-23T07:59:27.297Z"}

    @Indexed
    private String id;

    @Indexed
    private String symbol;
    private String name;

    private String twitter_url;

    private BigDecimal current_price;
    private BigDecimal high_24h;
    private BigDecimal low_24h;
    private BigDecimal price_change_24h;
    private BigDecimal price_change_percentage_24h;

    private int market_cap_rank;
    private BigDecimal market_cap;
    private BigDecimal market_cap_change_24h;
    private BigDecimal market_cap_change_percentage_24h;
    private BigDecimal total_volume;
    private BigDecimal circulating_supply;
    private BigDecimal total_supply;
    private BigDecimal max_supply;

    private BigDecimal ath;
    private BigDecimal ath_change_percentage;
    private Date ath_date;
    private BigDecimal atl;
    private BigDecimal atl_change_percentage;
    private Date atl_date;

    private Date last_updated;

    private long fully_diluted_valuation;

}
