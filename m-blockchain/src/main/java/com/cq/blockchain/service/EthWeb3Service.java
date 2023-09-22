package com.cq.blockchain.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.cq.blockchain.dao.ConfigEthDAO;
import com.cq.blockchain.dao.EventApprovalDAO;
import com.cq.blockchain.dao.EventCreatePairDAO;
import com.cq.blockchain.entity.ConfigEth;
import com.cq.blockchain.entity.EventApproval;
import com.cq.blockchain.entity.EventCreatePair;
import com.cq.util.MathUtil;
import com.cq.core.service.MailService;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.*;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
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
import java.util.stream.Collectors;

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
    private final EventApprovalDAO eventApprovalDAO;

    private final MailService mailService;

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
                        log.info("-----height----- {}", height);
                        
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

                Thread.sleep(1000);
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

    public Event event_Approval() {
        // event Approval(address indexed owner, address indexed spender, uint value);
        return new Event("Approval",
                Arrays.asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Address>(true) {
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

    public BigInteger contractTotalSupply(String address) throws IOException {
        Function f = new Function(
                "totalSupply",
                Arrays.asList(),
                Arrays.asList(new TypeReference<Uint256>() {
                })
        );
        return (BigInteger) readContract(address, f).getValue();
    }

    public BigInteger contractBalanceOf(String addressToken, String addressHolder) throws IOException {
        Function f = new Function(
                "balanceOf",
                Arrays.asList(new Address(addressHolder)),
                Arrays.asList(new TypeReference<Uint256>() {
                })
        );
        return (BigInteger) readContract(addressToken, f).getValue();
    }

    public int contractAllPairsLength(String address) throws IOException {
        Function f = new Function(
                "allPairsLength",
                Arrays.asList(),
                Arrays.asList(new TypeReference<Uint>() {
                })
        );
        return ((BigInteger) readContract(address, f).getValue()).intValue();
    }

    public String contractAllPairs(String addressFactory, int idx) throws IOException {
        Function f = new Function(
                "allPairs",
                Arrays.asList(new Uint(new BigInteger(idx + ""))),
                Arrays.asList(new TypeReference<Address>() {
                })
        );
        return (String) readContract(addressFactory, f).getValue();
    }

    public String contractPairToken(String addressPair, boolean token0OrToken1) throws IOException {
        Function f = new Function(
                token0OrToken1 ? "token0" : "token1",
                Arrays.asList(),
                Arrays.asList(new TypeReference<Address>() {
                })
        );
        return (String) readContract(addressPair, f).getValue();
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

    public boolean overliquidityLimit(EventCreatePair e, List<String> liquidityLimits) {
        boolean isMatch = false;
        for (String i : liquidityLimits) {
            String[] ss = i.split("-");
            String addressPegged = ss[0];
            BigDecimal amountLimit = new BigDecimal(ss[1]);

            if (e.getTokenAddressA().equalsIgnoreCase(addressPegged)) {
                isMatch = true;
                if (e.getAmountA().compareTo(amountLimit) < 0) {
                    return false;
                }
            }
            if (e.getTokenAddressB().equalsIgnoreCase(addressPegged)) {
                isMatch = true;
                if (e.getAmountB().compareTo(amountLimit) < 0) {
                    return false;
                }
            }

            if (isMatch) {
                break;
            }
        }
        if (!isMatch) {
            log.warn("overliquidityLimit not match ----- pari: {}, symbol: {}, token0: {}, amount0: {} - symbol: {}, token1: {}, amount1: {}",
                    e.getAddressPair(),
                    e.getSymbolA(),
                    e.getTokenAddressA(),
                    e.getAmountA().toString(),
                    e.getSymbolB(),
                    e.getTokenAddressB(),
                    e.getAmountB().toString()
            );
            return false;
        }

        return true;
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
            if (overliquidityLimit(e, liquidityLimits)) {
                return;
            }

            eventCreatePairDAO.save(e);
        });
    }

    private final static long INTERVAL_LOOP_ADD_LIQUIDITY = 5000L;

    public void scanNewPair(String addressFactory, List<String> liquidityLimits, List<String> notices) throws IOException {
        int allPairsLength = contractAllPairsLength(addressFactory);
        while (true) {
            do {
                try {
                    int allPairsLengthNew = contractAllPairsLength(addressFactory);
                    if (allPairsLengthNew <= allPairsLength) {
                        break;
                    }

                    for (int i = allPairsLength; i < allPairsLengthNew; ++i) {
                        try {
                            String pair = contractAllPairs(addressFactory, i);

                            String token0 = contractPairToken(pair, false);
                            String symbol0 = contractSymbol(token0);
                            int decimals0 = contractDecimals(token0);
                            BigDecimal balance0 = MathUtil.trimByDecimals(contractBalanceOf(token0, pair), decimals0);

                            String token1 = contractPairToken(pair, true);
                            String symbol1 = contractSymbol(token1);
                            int decimals1 = contractDecimals(token1);
                            BigDecimal balance1 = MathUtil.trimByDecimals(contractBalanceOf(token1, pair), decimals1);

                            EventCreatePair e = eventCreatePairDAO.findOneByAddressPair(pair);
                            if (e == null) {
                                e = new EventCreatePair();
                            }

                            e.setTokenAddressA(token0);
                            e.setSymbolA(symbol0);
                            e.setDecimalsA(decimals0);
                            e.setAmountA(balance0);

                            e.setTokenAddressB(token1);
                            e.setSymbolB(symbol1);
                            e.setDecimalsB(decimals1);
                            e.setAmountB(balance1);

                            e.setAddressPair(pair);

                            String subject = StrUtil.format("CreatePair: {} - {}", symbol0, symbol1);
                            String content = StrUtil.format("idx: {}, pari: {}, symbol: {}, token0: {}, amount0: {} - symbol: {}, token1: {}, amount1: {}", i, pair, symbol0, token0, balance0.toString(), symbol1, token1, balance1.toString());
                            log.info(content);

                            if (!overliquidityLimit(e, liquidityLimits)) {
                                continue;
                            }

                            eventCreatePairDAO.save(e);

                            mailService.sendMail(notices, subject, content);
                        } catch (Exception e) {
                            log.error(e.getMessage() + " - " + i, e);
                        }
                    }

                    allPairsLength = allPairsLengthNew;

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

            } while (false);

            ThreadUtil.sleep(INTERVAL_LOOP_ADD_LIQUIDITY);
        }
    }

    public void grabberWhaleApprove(List<String> whaleAddresses, List<String> notices) {
        Event event_Approval = event_Approval();
        final String signature_Approval = EventEncoder.encode(event_Approval);

        grabberTransaction().subscribe(t -> {
            List<String> w = whaleAddresses.stream().filter(a -> a.equalsIgnoreCase(t.getFrom())).collect(Collectors.toList());
            if (w.size() == 0) {
                return;
            }
            String whale = w.get(0);

            EventApproval exist = eventApprovalDAO.findOneByToken(t.getTo());
            if (exist != null) {
                return;
            }
            String token = t.getTo();
            String symbol = contractSymbol(token);
            int decimals = contractDecimals(token);
            BigDecimal totalSupply = MathUtil.trimByDecimals(contractTotalSupply(token), decimals);

            List<Log> logs = t.getLogs();
            if (CollectionUtil.isEmpty(logs)) {
                return;
            }

            boolean isApproval = false;
            for (int i = 0; i < logs.size(); ++i) {
                Log l = logs.get(i);
                List<String> topics = l.getTopics();
                if (CollectionUtil.isEmpty(topics)) {
                    continue;
                }
                if (!signature_Approval.equalsIgnoreCase(topics.get(0))) {
                    continue;
                }
                isApproval = true;
                break;

//                String owner = (String) FunctionReturnDecoder.decodeIndexedValue(topics.get(1), new TypeReference<Address>() {
//                }).getValue();
//                String spender = (String) FunctionReturnDecoder.decodeIndexedValue(topics.get(2), new TypeReference<Address>() {
//                }).getValue();

            }
            if (!isApproval) {
                return;
            }

            EventApproval e = eventApprovalDAO.findOneByTransactionHash(t.getTransactionHash());
            if (e == null) {
                e = new EventApproval();
            }
            e.setTransactionHash(t.getTransactionHash());
            e.setTransactionIndex(t.getTransactionIndex());
            e.setBlockHash(t.getBlockHash());
            e.setBlockNumber(t.getBlockNumber());
            e.setAddressFrom(whale);
            e.setToken(token);
            e.setSymbol(symbol);
            e.setDecimals(decimals);
            e.setTotalSupply(totalSupply);
            eventApprovalDAO.save(e);

            String subject = StrUtil.format("WhaleApprove - {}", symbol);
            String content = StrUtil.format("whale: {}, blockNumber: {}, contract: {}, symbol: {}, decimals: {}, totalSupply: {}", whale, e.getBlockNumber().toString(), token, symbol, decimals + "", totalSupply.toPlainString());
            content += StrUtil.format("\n\n https://bscscan.com/tx/{}", e.getTransactionHash());

            mailService.sendMail(notices, subject, content);
        });
    }

}
