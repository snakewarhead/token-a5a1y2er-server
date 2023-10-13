package com.cq.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cq.core.config.MqConfigCommand;
import com.cq.exchange.entity.ExchangeOrderBookDiff;
import com.cq.exchange.enums.ExchangeActionType;
import com.cq.exchange.enums.ExchangeEnum;
import com.cq.exchange.service.ExchangeOrderBookService;
import com.cq.exchange.vo.ExchangeRunningParam;
import com.cq.exchange.vo.ExchangeRunningParamMSG;
import com.cq.vo.JSONResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.PingMessage;
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

    private final RabbitTemplate rabbitTemplate;

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

            rabbitTemplate.convertAndSend(MqConfigCommand.EXCHANGE_NAME, mapRoutingKey(exchangeEnum.getCode(), actionType), paramMSG);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Scheduled(fixedDelay = 30000)
    public void ping() {
        for (ExchangeRunningParam p : mapSubscribed.keySet()) {
            Map<String, WebSocketSession> m = mapSubscribed.get(p);
            if (CollUtil.isEmpty(m)) {
                continue;
            }
            m.values().parallelStream().forEach(i -> {
                try {
                    if (!i.isOpen()) {
                        return;
                    }
                    i.sendMessage(new PingMessage());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    @Scheduled(fixedDelay = 600000)
    public void stale() {
        for (ExchangeRunningParam p : mapSubscribed.keySet()) {
            int countLive = 0;
            Map<String, WebSocketSession> m = mapSubscribed.get(p);
            if (CollUtil.isNotEmpty(m)) {
                for (Iterator<Map.Entry<String, WebSocketSession>> it = m.entrySet().iterator(); it.hasNext(); ) {
                    WebSocketSession s = it.next().getValue();
                    if (!s.isOpen()) {
                        it.remove();
                        continue;
                    }
                    ++countLive;
                }
            }

            if (countLive > 0) {
                continue;
            }

            // unsubscribe
            ExchangeRunningParamMSG paramMSG = new ExchangeRunningParamMSG(ExchangeRunningParamMSG.UNSUBSCRIBE, p);
            ExchangeActionType actionType = ExchangeActionType.getEnum(p.getAction().getName());
            rabbitTemplate.convertAndSend(MqConfigCommand.EXCHANGE_NAME, mapRoutingKey(exchangeEnum.getCode(), actionType), paramMSG);
        }
    }

    @RabbitListener(
            bindings = {@QueueBinding(
                    value = @Queue(name = MqConfigCommand.QUEUE_NAME_NOTIFY_ORDERBOOK_DIFF, durable = "false"),
                    exchange = @Exchange(name = MqConfigCommand.EXCHANGE_NAME, durable = "false"),
                    key = {MqConfigCommand.ROUTING_KEY_NOTIFY_ORDERBOOK_DIFF})})
    public void orderBookDiff(ExchangeOrderBookDiff o) {
        try {
            ExchangeRunningParam p = new ExchangeRunningParam(o.getExchangeId(), o.getTradeType()).putAction(ExchangeActionType.OrderBook, o.getSymbol(), null);
            Map<String, WebSocketSession> m = mapSubscribed.get(p);
            if (CollUtil.isEmpty(m)) {
                return;
            }
            var res = JSONResult.success("", o);

            m.values().parallelStream().forEach(i -> {
                try {
                    if (!i.isOpen()) {
                        return;
                    }
                    i.sendMessage(new TextMessage(res.toJSONString()));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            // do not requeue to mq
            log.error(e.getMessage(), e);
        }
    }

    private String mapRoutingKey(int exchange, ExchangeActionType action) {
        if (ExchangeEnum.BINANCE.is(exchange)) {
            return action.isGrabber() ? MqConfigCommand.ROUTING_KEY_BINANCE_GRABBER : MqConfigCommand.ROUTING_KEY_ANALYSER;
        }

        throw new IllegalStateException("Unexpected value: " + exchange);
    }
}
