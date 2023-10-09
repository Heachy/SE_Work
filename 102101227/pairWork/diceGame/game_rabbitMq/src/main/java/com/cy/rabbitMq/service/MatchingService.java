package com.cy.rabbitMq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchingService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void putQueue(String msg) {
        // 定义交换机
        String exchangeName = "match_exchange";
        // 路由key
        String routeKey = "match";
        rabbitTemplate.convertAndSend(exchangeName, routeKey, msg);
    }

    public Object getMsg(){
        return  rabbitTemplate.receiveAndConvert("match_queue");
    }
}
