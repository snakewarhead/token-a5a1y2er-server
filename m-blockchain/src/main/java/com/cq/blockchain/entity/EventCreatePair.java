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
@Document(collection = "blockchain_event_create_pair")
public class EventCreatePair extends BaseEntity<EventCreatePair> {

    @Indexed
    private String transactionHash;
    private BigInteger transactionIndex;
    @Indexed
    private String blockHash;
    private BigInteger blockNumber;

    @Indexed
    private String tokenAddressA;
    private String symbolA;
    private Integer decimalsA;

    @Indexed
    private String tokenAddressB;
    private String symbolB;
    private Integer decimalsB;

    @Indexed
    private String addressPair;

    private BigDecimal amountA;
    private BigDecimal amountB;

}
