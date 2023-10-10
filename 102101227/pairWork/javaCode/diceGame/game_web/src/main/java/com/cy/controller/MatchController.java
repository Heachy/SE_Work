package com.cy.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.cy.common.api.CommonResult;
import com.cy.rabbitMq.service.MatchingService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/match")
public class MatchController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MatchingService matchingService;

    public MatchController(RedisTemplate<String, Object> redisTemplate, MatchingService matchingService) {
        this.redisTemplate = redisTemplate;
        this.matchingService = matchingService;
    }

    @GetMapping("/connect")
    public CommonResult<Map<String,Object>> open() {

        System.out.println("开始匹配");

        Map<String,Object> map=new HashMap<>();

        map.put("loginId",StpUtil.getLoginId());



        map.put("roomId",StpUtil.getLoginId());

        if(redisTemplate.opsForValue().get(StpUtil.getLoginId().toString()) != null){
            map.put("isConnect",false);
            System.out.println("已经在匹配中");
            map.put("msg","已经在匹配中");
            return CommonResult.success(map);
        }

        Object msg = matchingService.getMsg();

        if(msg != null){
            map.put("isConnect",true);

            System.out.println("匹配成功");

            System.out.println(msg);

            map.put("roomId",msg.toString());
            map.put("msg","匹配成功");

            redisTemplate.delete(msg.toString());

        }else{
            map.put("isConnect",false);
            matchingService.putQueue(StpUtil.getLoginId().toString());
            System.out.println("进入匹配队列");
            map.put("msg","进入匹配队列");

            redisTemplate.opsForValue().set(StpUtil.getLoginId().toString(), "匹配中");
        }
        return CommonResult.success(map);
    }

    @PostMapping("/disconnect")
    public CommonResult<String> disconnect() {

        System.out.println("取消匹配");

        redisTemplate.delete(StpUtil.getLoginId().toString());

        Object msg = matchingService.getMsg();

        while(msg!=null){
            if (msg.toString().equals(StpUtil.getLoginId().toString())){
                break;
            }else{
                matchingService.putQueue(msg.toString());
                msg = matchingService.getMsg();
            }
        }

        return CommonResult.success("取消匹配");
    }
}
