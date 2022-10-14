package com.brecycle.service;

import com.brecycle.entity.dto.UserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author cmgun
 */
public interface UserService {

    /**
     * 登录
     *
     * @param userName
     * @param password
     * @return
     */
    UserInfo login(String userName, String password) throws Exception;

    /**
     * 获取用户信息
     * @return
     */
    UserInfo getInfo(HttpServletRequest request);
}
