package com.cq.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cq.exchange.entity.ExchangeOrderBook;
import com.cq.exchange.enums.ExchangeActionType;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.service.ExchangeOrderBookService;
import com.cq.exchange.vo.ExchangeRunningParam;
import com.cq.exchange.vo.ExchangeRunningParamMSG;
import com.cq.vo.JSONResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class WSSessionPublisher {

    private final ExchangeOrderBookService exchangeOrderBookService;

    @Value("${exchange}")
    private String exchange;
    private ExchangeEnum exchangeEnum;

    private Map<ExchangeRunningParam, Map<String, WebSocketSession>> mapSubscribed = new HashMap<>();

    @PostConstruct
    public void init() {
        exchangeEnum = ExchangeEnum.getEnum(exchange);
        if (exchangeEnum == null) {
            throw new RuntimeException("exchange not found");
        }
    }

    public void receive(WebSocketSession session, String msg) {
        if (session == null || !session.isOpen()) {
            log.warn("session is closed. {}", session.getId());
            return;
        }
        if (StrUtil.isEmpty(msg)) {
            return;
        }

        try {
            ExchangeRunningParamMSG paramMSG = ExchangeRunningParamMSG.parse(msg);
            if (paramMSG == null) {
                log.error("msg format error 1. {}", msg);
                return;
            }
            ExchangeRunningParam param = paramMSG.getParam();
            if (param == null) {
                log.error("msg format error 2. {}", msg);
                return;
            }
            if (param.getExchange() != exchangeEnum.getCode()) {
                log.error("exchange not match {}", msg);
                return;
            }
            ExchangeRunningParam.Action action = param.getAction();
            if (action == null) {
                log.error("msg format error 3. {}", msg);
                return;
            }
            ExchangeActionType actionType = ExchangeActionType.getEnum(action.getName());
            if (actionType == null) {
                log.error("msg action type error. {}", msg);
                return;
            }

            Map<String, WebSocketSession> map = mapSubscribed.get(param);
            if (paramMSG.isSubscribe()) {
                if (map == null) {
                    map = new HashMap<>();
                    mapSubscribed.put(param, map);
                }
                map.put(session.getId(), session);
            } else {
                if (map == null) {
                    return;
                }
                map.remove(session.getId());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void pushOrderBook() {
        for (ExchangeRunningParam p : mapSubscribed.keySet()) {
            if (ExchangeActionType.OrderBook.isNot(p.getAction().getName())) {
                continue;
            }

            ExchangeOrderBook e = exchangeOrderBookService.find(p.getExchange(), p.getTradeType(), p.getAction().getSymbols().get(0));
            if (e == null) {
//                log.warn("no data. need grab first. {}", p.toString());
                continue;
            }
            JSONResult<ExchangeOrderBook> res = JSONResult.success("", e);

            // closed session should be removed
            Map<String, WebSocketSession> m = mapSubscribed.get(p);
            if (CollUtil.isEmpty(m)) {
                continue;
            }
            for (Iterator<Map.Entry<String, WebSocketSession>> it = m.entrySet().iterator(); it.hasNext(); ) {
                WebSocketSession s = it.next().getValue();
                if (!s.isOpen()) {
                    it.remove();
                    continue;
                }
                try {
                    s.sendMessage(new TextMessage(res.toJSONString()));
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }

}
