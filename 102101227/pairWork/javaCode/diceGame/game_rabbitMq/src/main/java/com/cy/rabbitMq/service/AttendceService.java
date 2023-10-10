package com.cy.rabbitMq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendceService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     *  1: 定义交换机
     */
    private final String exchangeName = "test_direct";
    /**
     *  2: 路由key
     */
    private final String routeKey = "attendance";

    public void issueCheck(String msg) {
        rabbitTemplate.convertAndSend(exchangeName,routeKey, msg);
    }

    public Object getMsg(){
        return  rabbitTemplate.receiveAndConvert("dead_direct");
    }
}
