package com.brecycle.controller.hanlder;

import com.brecycle.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

/**
 * 统一异常处理
 *
 * @author cmgun
 */
@Slf4j
@ControllerAdvice
public class ErrorHandlerController {

    /**
     * 登录异常
     *
     * @param request 请求
     * @param exception 异常
     * @return 响应
     */
    @ExceptionHandler(LoginException.class)
    @ResponseBody
    public Response loginExceptionHandler(HttpServletRequest request, LoginException exception) {
        log.warn("LoginException，url:{}", request.getRequestURI(), exception);
        return Response.builder()
                .code(Response.UNAUTHORIZED)
                .message(exception.getMessage())
                .build();
    }

    /**
     * 其他异常
     *
     * @param request 请求
     * @param exception 异常
     * @return 响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response exceptionHandler(HttpServletRequest request, Exception exception) {
        log.warn("ExceptionHandler，url:{}", request.getRequestURI(), exception);
        return Response.builder()
                .code(Response.ERROR)
                .message("系统内部异常")
                .build();
    }
}
