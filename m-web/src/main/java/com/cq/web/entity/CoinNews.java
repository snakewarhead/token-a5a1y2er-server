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

    private String content;
    private Date date;
}
