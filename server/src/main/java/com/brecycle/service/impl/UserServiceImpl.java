package com.brecycle.service.impl;

import com.brecycle.config.redis.RedisConstant;
import com.brecycle.config.redis.RedisUtil;
import com.brecycle.entity.Resource;
import com.brecycle.entity.Role;
import com.brecycle.entity.User;
import com.brecycle.entity.dto.UserInfo;
import com.brecycle.mapper.ResourceMapper;
import com.brecycle.mapper.RoleMapper;
import com.brecycle.mapper.UserMapper;
import com.brecycle.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户模块块
 *
 * @author cmgun
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public UserInfo login(String userName, String password) throws Exception {
        // 是否已经锁定
        String lockKey = RedisConstant.CACHE_PREFIX + RedisConstant.USER_TOKEN_KEY + RedisConstant.USER_LOCK_KEY + userName;
        if (redisUtil.hasKey(lockKey)) {
            Long lock = (Long) redisUtil.getCacheObject(lockKey);
            if (lock > 5) {
                throw new LoginException("登录失败次数过多，账户已锁定");
            }
        }
        User user = userMapper.selectByUserName(userName);
        if (Objects.isNull(user)) {
            throw new LoginException("登录失败，账户或密码不正确");
        }
        if (!StringUtils.equals(password, user.getPassword())) {
            if (redisUtil.hasKey(lockKey)) {
                redisUtil.incr(lockKey);
                // 过期时间1小时
                redisUtil.expire(lockKey, 1, TimeUnit.HOURS);
            }
            throw new LoginException("登录失败，账户或密码不正确");
        }
        // 查找角色和菜单资源
        List<Role> roleList = roleMapper.selectByUserId(user.getId());
        List<Resource> resourceList = resourceMapper.selectByRoleIds(roleList.stream().map(Role::getId).collect(Collectors.toList()));

        // TODO 设置jwt token
        return UserInfo.builder()
                .userName(user.getName())
                .role(roleList.stream().map(Role::getKey).collect(Collectors.toList()))
                .resources(resourceList)
                .build();
    }
}
