package com.brecycle.service.impl;

import com.brecycle.entity.dto.UserInfo;
import com.brecycle.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户模块块
 *
 * @author cmgun
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserInfo login(String userName, String password) {
        return UserInfo.builder().userName("test111").build();
    }
}
