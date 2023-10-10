package com.cy.websocket.controller;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Haechi
 * @date 2022/5/23 16:27
 *&#064;Description  WebSocket操作类
 */
@ServerEndpoint("/websocketServer/{roomId}/{userId}")
@Component
@Slf4j
@Data
public class WebSocketController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static Map<String,WebSocketController> userMap = new ConcurrentHashMap<>();
    private static Map<String, Set<WebSocketController>> roomMap = new ConcurrentHashMap<>();
    private Session session;
    private String userId;

    @OnOpen
    public void onOpen(Session session , @PathParam("roomId") String roomId , @PathParam("userId") String userId) {
        logger.info("现在来连接的房间id："+roomId+"用户名："+userId);
        this.session = session;
        this.userId = userId;
        userMap.put(userId,this);
        if (!roomMap.containsKey(roomId)) {
            Set<WebSocketController> set = new HashSet<>();
            set.add(userMap.get(userId));
            roomMap.put(roomId,set);
        } else {
            roomMap.get(roomId).add(this);
        }
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId , @PathParam("roomId") String roomId) {
        if (roomMap.containsKey(roomId)) {
            Set<WebSocketController> set = roomMap.get(roomId);
            set.removeIf(item -> item.userId.equals(userId));
        }
    }

    @OnMessage
    public void onMessage(String message , @PathParam("roomId") String roomId , @PathParam("userId") String userId) {
        sendMessageTo(message,roomId,userId);
    }

    /**
     * 群聊
     * @param message 消息
     * @param roomId 房间号
     * @param userId 发送人
     */
    public void sendMessageTo(String message , String roomId , String userId) {
        if (roomMap.containsKey(roomId)) {
            for (WebSocketController item : roomMap.get(roomId)) {
                if (!item.userId.equals(userId)) {
                    item.session.getAsyncRemote().sendText(message);
                }
            }
        }
    }

    /**
     * 私聊
     * @param message 消息
     * @param toUserId 接收人
     */
    public void sendUserTo(String message , String toUserId) {
        if (userMap.containsKey(toUserId)) {
            userMap.get(toUserId).session.getAsyncRemote().sendText(message);
        }
    }
}

