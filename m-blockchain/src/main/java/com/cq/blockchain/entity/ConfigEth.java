package com.cq.blockchain.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Created by lin on 2020-09-23.
 */
@Data
@Accessors(chain = true)
@Document(collection = "blockchain_config_eth")
public class ConfigEth {

    @MongoId
    private Integer id;

    private Long currentBlockHeight;
}
