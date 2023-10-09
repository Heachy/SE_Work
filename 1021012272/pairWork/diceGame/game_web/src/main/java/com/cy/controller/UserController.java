package com.cy.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cy.common.api.CommonResult;
import com.cy.generated.domain.GameUser;
import com.cy.generated.service.GameUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final GameUserService gameUserService;

    public UserController(GameUserService gameUserService) {
        this.gameUserService = gameUserService;
    }

    @PostMapping("/logon")
    public CommonResult<String> logon(@RequestBody Map<String, String> form){

        System.out.println(form);

        gameUserService.save(new GameUser(form.get("phone"), form.get("password")));

        return CommonResult.success("注册成功");
    }

    @PostMapping("/login")
    public CommonResult<Map<String,Object>> login(@RequestBody Map<String, String> form){

        System.out.println(form);

        QueryWrapper<GameUser> wrapper = new QueryWrapper<>();

        wrapper.eq("phone", form.get("phone"));

        GameUser user = gameUserService.getOne(wrapper);

        Map<String,Object> map=new HashMap<>();

        if (user == null) {

            return CommonResult.failed("用户不存在");
        }

        if (!user.getPassword().equals(form.get("password"))) {
            return CommonResult.failed("密码错误");
        }else{

            StpUtil.login(user.getId(),true);

            SaTokenInfo info = StpUtil.getTokenInfo();

            map.put("tokenValue",info.tokenValue);

            map.put("user",user);

            map.put("msg","登录成功");

            return CommonResult.success(map);
        }
    }
}
