package com.cy.controller;

import com.cy.util.CrawlUtil;
import com.cy.util.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Haechi
 */
@RestController
@RequestMapping("/barrage")
public class BarrageController {
    @GetMapping("/get")
    public ResultUtil getData(){
        return new ResultUtil(200,"获得成功", CrawlUtil.getBarrage());
    }
}
