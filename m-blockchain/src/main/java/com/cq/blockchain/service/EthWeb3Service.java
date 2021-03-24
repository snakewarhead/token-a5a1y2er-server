package com.cq.blockchain.service;

import cn.hutool.core.collection.CollectionUtil;
import com.cq.blockchain.dao.ConfigEthDAO;
import com.cq.blockchain.dao.EventCreatePairDAO;
import com.cq.blockchain.entity.ConfigEth;
import com.cq.blockchain.entity.EventCreatePair;
import com.cq.blockchain.util.MathUtil;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.*;
import org.web3j.abi.datatypes.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lin on 2021-03-18.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EthWeb3Service {

    private final Web3j web3j;

    private final ConfigEthDAO configEthDAO;
    private final EventCreatePairDAO eventCreatePairDAO;

    public String getClientVersion() throws IOException {
        return web3j.web3ClientVersion().send().getWeb3ClientVersion();
    }

    public BigInteger ethBlockNumber() throws IOException {
        return web3j.ethBlockNumber().send().getBlockNumber();
    }

    public int ethGetBlockTransactionCountByNumber(long height) throws IOException {
        return web3j.ethGetBlockTransactionCountByNumber(new DefaultBlockParameterNumber(height)).send().getTransactionCount().intValue();
    }

    public Transaction ethGetTransactionByBlockNumberAndIndex(long height, int idx) throws IOException {
        return web3j.ethGetTransactionByBlockNumberAndIndex(new DefaultBlockParameterNumber(height), BigInteger.valueOf(idx)).send().getTransaction().orElse(null);
    }

    public TransactionReceipt ethGetTransactionReceipt(String transactionHash) throws IOException {
        return web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt().orElse(null);
    }

    public void grabberEventAddLiquidityByFilter(String contractAddress) {
        // event PairCreated(address indexed token0, address indexed token1, address pair, uint);
        Event event = new Event("PairCreated",
                Arrays.asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Address>(false) {
                        },
                        new TypeReference<Uint>(false) {
                        }
                )
        );

//        BigInteger currentBlockNumber = ethBlockNumber();
        BigInteger currentBlockNumber = BigInteger.valueOf(5807504L);
        EthFilter ethFilter = new EthFilter(DefaultBlockParameter.valueOf(currentBlockNumber), DefaultBlockParameterName.LATEST, contractAddress);
        ethFilter.addSingleTopic(EventEncoder.encode(event));
        web3j.ethLogFlowable(ethFilter).subscribe(i -> {
            try {
                String txid = i.getTransactionHash();

                EventValues eventValues = Contract.staticExtractEventParameters(event, i);
                Address tokenA = (Address) eventValues.getIndexedValues().get(0);
                Address tokenB = (Address) eventValues.getIndexedValues().get(1);
                Address pair = (Address) eventValues.getNonIndexedValues().get(0);
                Uint allPairLength = (Uint) eventValues.getNonIndexedValues().get(1);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    public Flowable<TransactionReceipt> grabberTransaction() {
        return Flowable.create(subscriber -> {
            ConfigEth c = configEthDAO.findById(1).orElse(new ConfigEth().setId(1).setCurrentBlockHeight(-1L));

            long height = c.getCurrentBlockHeight();
            if (c.getCurrentBlockHeight() == -1L) {
                height = ethBlockNumber().longValue();
            }

            while (true) {
                try {
                    while (height < ethBlockNumber().longValue()) {
                        int trxCount = ethGetBlockTransactionCountByNumber(height);
                        for (int i = 0; i < trxCount; ++i) {
                            Transaction trx = ethGetTransactionByBlockNumberAndIndex(height, i);
                            if (trx == null) {
                                log.warn("trx is null");
                                continue;
                            }
                            TransactionReceipt rec = ethGetTransactionReceipt(trx.getHash());
                            if (rec == null) {
                                continue;
                            }
                            subscriber.onNext(rec);
                        }

                        ++height;
                        c.setCurrentBlockHeight(height);
                        configEthDAO.save(c);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    subscriber.onError(e);
                }

                Thread.sleep(10 * 1000);
            }
        }, BackpressureStrategy.BUFFER);
    }

    public Event event_pairCreated() {
        // event PairCreated(address indexed token0, address indexed token1, address pair, uint);
        return new Event("PairCreated",
                Arrays.asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Address>(false) {
                        },
                        new TypeReference<Uint>(false) {
                        }
                )
        );
    }

    public Event event_Mint() {
        // event Mint(address indexed sender, uint amount0, uint amount1);
        return new Event("Mint",
                Arrays.asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Uint>(false) {
                        },
                        new TypeReference<Uint>(false) {
                        }
                )
        );
    }

    public String contractSymbol(String address) throws IOException {
        Function f = new Function(
                "symbol",
                Arrays.asList(),
                Arrays.asList(new TypeReference<Utf8String>() {
                })
        );
        return (String) readContract(address, f).getValue();
    }

    public int contractDecimals(String address) throws IOException {
        Function f = new Function(
                "decimals",
                Arrays.asList(),
                Arrays.asList(new TypeReference<Uint>() {
                })
        );
        return ((BigInteger) readContract(address, f).getValue()).intValue();
    }

    public Type<?> readContract(String address, Function function) throws IOException {
        String data = FunctionEncoder.encode(function);
        return FunctionReturnDecoder.decode(
                web3j.ethCall(
                        org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(null, address, data),
                        DefaultBlockParameterName.LATEST
                ).send().getValue(),
                function.getOutputParameters()
        ).get(0);
    }

    public void grabberEventAddLiquidity(String addressRouter, String addressFactory, List<String> liquidityLimits) {
        Event event_PairCreated = event_pairCreated();
        Event event_Mint = event_Mint();
        final String signature_PairCreated = EventEncoder.encode(event_PairCreated);
        final String signature_Mint = EventEncoder.encode(event_Mint);

        grabberTransaction().subscribe(t -> {
            if (!addressRouter.equalsIgnoreCase(t.getTo())) {
                return;
            }

            List<Log> logs = t.getLogs();
            if (CollectionUtil.isEmpty(logs)) {
                return;
            }

            EventCreatePair e = eventCreatePairDAO.findOneByTransactionHash(t.getTransactionHash());
            if (e == null) {
                e = new EventCreatePair();
            }
            e.setTransactionHash(t.getTransactionHash());
            e.setTransactionIndex(t.getTransactionIndex());
            e.setBlockHash(t.getBlockHash());
            e.setBlockNumber(t.getBlockNumber());

            int idxPairCreate = -1;
            int idxMint = -1;
            for (int i = 0; i < logs.size(); ++i) {
                Log l = logs.get(i);
                List<String> topics = l.getTopics();
                if (CollectionUtil.isEmpty(topics)) {
                    continue;
                }

                if (idxPairCreate == -1 && signature_PairCreated.equalsIgnoreCase(topics.get(0))) {
                    if (!addressFactory.equalsIgnoreCase(l.getAddress())) {
                        continue;
                    }
                    idxPairCreate = i;

                    String tokenAddressA = (String) FunctionReturnDecoder.decodeIndexedValue(topics.get(1), new TypeReference<Address>() {
                    }).getValue();
                    String symbolA = contractSymbol(tokenAddressA);
                    int decimalsA = contractDecimals(tokenAddressA);
                    String tokenAddressB = (String) FunctionReturnDecoder.decodeIndexedValue(topics.get(2), new TypeReference<Address>() {
                    }).getValue();
                    String symbolB = contractSymbol(tokenAddressB);
                    int decimalsB = contractDecimals(tokenAddressB);

                    // pair address
                    String addressPair = (String) FunctionReturnDecoder.decode(l.getData(), event_PairCreated.getNonIndexedParameters()).get(0).getValue();

                    e.setTokenAddressA(tokenAddressA);
                    e.setSymbolA(symbolA);
                    e.setDecimalsA(decimalsA);

                    e.setTokenAddressB(tokenAddressB);
                    e.setSymbolB(symbolB);
                    e.setDecimalsB(decimalsB);

                    e.setAddressPair(addressPair);

                    continue;
                }

                if (idxMint == -1 && signature_Mint.equalsIgnoreCase(topics.get(0))) {
                    if (!e.getAddressPair().equalsIgnoreCase(l.getAddress())) {
                        continue;
                    }
                    idxMint = i;

                    List<Type> ls = FunctionReturnDecoder.decode(l.getData(), event_Mint.getNonIndexedParameters());
                    BigInteger amountA = (BigInteger) ls.get(0).getValue();
                    BigInteger amountB = (BigInteger) ls.get(1).getValue();

                    e.setAmountA(MathUtil.trimByDecimals(amountA, e.getDecimalsA()));
                    e.setAmountB(MathUtil.trimByDecimals(amountB, e.getDecimalsB()));

                    break;
                }
            }

            if (e.getAmountA() == null || e.getAmountB() == null) {
                return;
            }

            // store over the limit
            boolean isMatch = false;
            for (String i : liquidityLimits) {
                String[] ss = i.split("-");
                String addressPegged = ss[0];
                BigDecimal amountLimit = new BigDecimal(ss[1]);

                if (e.getTokenAddressA().equalsIgnoreCase(addressPegged)) {
                    isMatch = true;
                    if (e.getAmountA().compareTo(amountLimit) < 0) {
                        return;
                    }
                }
                if (e.getTokenAddressB().equalsIgnoreCase(addressPegged)) {
                    isMatch = true;
                    if (e.getAmountB().compareTo(amountLimit) < 0) {
                        return;
                    }
                }

                if (isMatch) {
                    break;
                }
            }
            if (!isMatch) {
                return;
            }

            eventCreatePairDAO.save(e);
        });
    }
}
