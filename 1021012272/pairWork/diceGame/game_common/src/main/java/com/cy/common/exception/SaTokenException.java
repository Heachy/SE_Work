package com.cy.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.util.SaResult;
import com.cy.common.api.CommonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.message.AuthException;

@RestControllerAdvice
public class SaTokenException {
    // 全局异常拦截（拦截项目中的NotLoginException异常）
    @ExceptionHandler(NotLoginException.class)
    public SaResult handlerNotLoginException(NotLoginException nle) {

        // 打印堆栈，以供调试
        nle.printStackTrace();

        // 判断场景值，定制化异常信息
        String message = "";
        switch (nle.getType()) {
            case NotLoginException.NOT_TOKEN:
                message = "未提供token";
                break;
            case NotLoginException.INVALID_TOKEN:
                message = "token无效";
                break;
            case NotLoginException.TOKEN_TIMEOUT:
                message = "token已过期";
                break;
            case NotLoginException.BE_REPLACED:
                message = "token已被顶下线";
                break;
            case NotLoginException.KICK_OUT:
                message = "token已被踢下线";
                break;
            default:
                message = "当前会话未登录";
                break;
        }

        // 返回给前端
        return SaResult.error(message);
    }

    @ExceptionHandler(AuthException.class)
    public CommonResult<String> myException(AuthException ae) {
        ae.printStackTrace();

        return CommonResult.forbidden("没有权限操作");
    }

    @ExceptionHandler(NotRoleException.class)
    public CommonResult<String> myRoleException(NotRoleException ae) {
        ae.printStackTrace();

        return CommonResult.forbidden("没有权限操作");
    }
}
