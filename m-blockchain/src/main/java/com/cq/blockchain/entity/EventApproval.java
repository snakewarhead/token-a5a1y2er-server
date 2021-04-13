package com.cq.blockchain.entity;

import com.cq.core.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by lin on 2021-03-23.
 */
@Data
@Document(collection = "blockchain_event_approval")
public class EventApproval extends BaseEntity<EventApproval> {

    @Indexed
    private String transactionHash;
    private BigInteger transactionIndex;
    @Indexed
    private String blockHash;
    private BigInteger blockNumber;

    @Indexed
    private String addressFrom;

    private String token;
    private String symbol;
    private Integer decimals;
    private BigDecimal totalSupply;

}
