package com.cy.rabbitMq.controller;

import com.cy.common.api.CommonResult;
import com.cy.rabbitMq.service.AttendceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Haechi
 */
@RestController
@RequestMapping("/attend")
public class AttendController {

    private final AttendceService attendceService;

    public AttendController(AttendceService attendceService) {
        this.attendceService = attendceService;
    }

    @RequestMapping("/open")
    public CommonResult<String> open() {

        System.out.println("开始点名");

        attendceService.issueCheck("点名已开启");

        return CommonResult.success("点名已开启");
    }

    @RequestMapping("/get")
    public CommonResult<String> get() {

        System.out.println("开始点名");

        Object msg = attendceService.getMsg();

        return CommonResult.success(msg.toString());
    }

}
