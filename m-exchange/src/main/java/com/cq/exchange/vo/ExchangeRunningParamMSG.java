package com.cq.exchange.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExchangeRunningParamMSG implements Serializable {

    /**
     * 0 - unsubscribe, 1 - subscribe
     */
    private int subscribe;

    private ExchangeRunningParam param;
}
