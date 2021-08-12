package com.cq.web.entity;

import com.cq.core.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "coin_news")
public class CoinNews extends BaseEntity<CoinNews> {

    @Indexed
    private String coin_id;
    private String symbol;

    @Indexed
    private String channel;
    @Indexed
    private Integer type = 0;

    private String content;
    private Date time;

    private String param0;
    private String param1;
    private String param2;
    private String param3;
    private String param4;

}
