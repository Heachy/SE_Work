package com.cy.rabbitMq.service;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Service;

@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(value = "check_dead",autoDelete = "false"),
                exchange = @Exchange(value = "dead_exchange",type = ExchangeTypes.DIRECT)
))
@Service
public class DeadQueService {

    @RabbitHandler
    public void receive(String msg) {
        System.out.println("收到消息：" + msg);
    }
}
