package com.cq.blockchain.service;

import com.cq.blockchain.dao.ConfigEthDAO;
import com.cq.blockchain.dao.TransactionEthDAO;
import com.cq.blockchain.entity.ConfigEth;
import com.cq.blockchain.entity.TransactionEth;
import com.cq.blockchain.util.ETHUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2020-09-23.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EthService {

    private final ConfigEthDAO configEthDAO;
    private final TransactionEthDAO transactionEthDAO;

    public void detect(String ip, String port, String password, long heightInit) throws Exception {
        ConfigEth c = configEthDAO.findById(1).orElse(null);
        if (c == null) {
            log.error("ConfigEth is null");
            return;
        }

        long heightCurr = c.getCurrentBlockHeight();
        heightCurr = Long.max(heightInit, heightCurr);

        if (heightCurr == 0) {
            log.error("block height has not been config yet, 0");
            return;
        }

        ETHUtils u = new ETHUtils("", "", ip, port, password);
        log.info(u.eth_blockNumberValue() + "");

        while (heightCurr < u.eth_blockNumberValue()) {
            log.info("----------- block height {} -------------", heightCurr);

            c.setCurrentBlockHeight(heightCurr);
            configEthDAO.save(c);

            long count = u.eth_getBlockTransactionCountByNumberValue(heightCurr);
            for (int i = 0; i < count; i++) {
                TransactionEth te = new TransactionEth();
                try {
                    // transaction
                    JSONObject t = u.getTransactionByBlockNumberAndIndex(heightCurr, i);
                    if (!t.containsKey("result")) {
                        log.error("getTransactionByBlockNumberAndIndex {} {}, result is null", heightCurr, i);
                        continue;
                    }
                    JSONObject tr = t.getJSONObject("result");

                    String txid = tr.getString("hash");

                    // to update
                    TransactionEth teOld = transactionEthDAO.findOneByTransactionHash(txid);
                    if (teOld != null) {
                        te = teOld;
                    }

                    te.setBlockHash(tr.getString("blockHash"));
                    te.setBlockNumber(Numeric.toBigInt(tr.getString("blockNumber")).longValue());
                    te.setTransactionIndex(Numeric.toBigInt(tr.getString("transactionIndex")).intValue());
                    te.setNonce(tr.getString("nonce"));

                    te.setTransactionHash(txid);
                    te.setFrom(tr.getString("from"));
                    te.setTo(tr.getString("to"));
                    te.setValue(u.parseEthAmount(tr.getString("value")));
                    te.setGasLimit(new BigDecimal(Numeric.toBigInt(tr.getString("gas"))));
                    te.setGasPrice(new BigDecimal(Numeric.toBigInt(tr.getString("gasPrice"))));

                    // transaction inner
                    JSONObject inner = u.eth_getTransactionReceipt(te.getTransactionHash());
                    if (!inner.containsKey("result")) {
                        log.error("eth_getTransactionReceipt {}, result is null", te.getTransactionHash());
                        continue;
                    }
                    JSONObject ir = inner.getJSONObject("result");

                    te.setStatus(ir.containsKey("status") ? ir.getString("status") : "");
                    te.setGasUsed(new BigDecimal(Numeric.toBigInt(ir.getString("gasUsed"))));

                    // fee
                    BigDecimal fee = te.getGasUsed().multiply(te.getGasPrice());
                    te.setTransactionFee(fee.divide(new BigDecimal(10).pow(18)));

                    // logs
                    List<TransactionEth.TransactionInner> lsTeti = new ArrayList<>();
                    JSONArray logs = ir.getJSONArray("logs");
                    for (int j = 0; j < logs.size(); ++j) {
                        JSONObject l = logs.getJSONObject(j);

                        TransactionEth.TransactionInner teti = te.new TransactionInner();
                        teti.setContract(l.getString("address"));
                        teti.setData(l.getString("data"));
                        teti.setLogIndex(l.getString("logIndex"));

                        // topics
                        List<String> tetitp = new ArrayList<>();
                        JSONArray lt = l.getJSONArray("topics");
                        for (int k = 0; k < lt.size(); ++k) {
                            tetitp.add(lt.getString(k));
                        }
                        teti.setTopics(tetitp);

                        lsTeti.add(teti);
                    }
                    te.setTransactionInnerList(lsTeti);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    te.setRemark(String.format("blockNumber:%d, transactionIndex:%d, exception: %s", heightCurr, i, e.getMessage()));
                }

                transactionEthDAO.save(te);
            }

            ++heightCurr;
        }
    }

    public void analyzeTransactionFeeRank(String interval) {

    }
}
