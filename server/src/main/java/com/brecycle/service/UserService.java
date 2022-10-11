package com.brecycle.service;

import com.brecycle.entity.dto.UserInfo;

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

}
