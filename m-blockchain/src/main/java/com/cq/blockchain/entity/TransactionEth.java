package com.cq.blockchain.entity;

import com.cq.core.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lin on 2020-09-23.
 */
@Data
@Document(collection = "blockchain_transaction_eth")
public class TransactionEth extends BaseEntity<TransactionEth> {

    private String remark;

    @Indexed
    private String blockHash;
    @Indexed
    private Long blockNumber;
    private Integer transactionIndex;

    @Indexed(unique = true)
    private String transactionHash;

    private String nonce;
    private String status;
//    private Date timestamp;

    @Indexed
    private String from;
    @Indexed
    private String to;

    private BigDecimal value;
    private BigDecimal gasPrice;
    private BigDecimal gasLimit;
    private BigDecimal gasUsed;
    private BigDecimal transactionFee;

    private List<TransactionInner> transactionInnerList;

    @Data
    public class TransactionInner {
        private String contract;
        private List<String> topics;
        private String data;
        private String logIndex;
    }

}
