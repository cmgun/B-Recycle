package com.brecycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.brecycle.config.FiscoBcos;
import com.brecycle.config.redis.RedisConstant;
import com.brecycle.config.redis.RedisUtil;
import com.brecycle.config.shiro.JWTConfig;
import com.brecycle.config.shiro.JwtTokenUtil;
import com.brecycle.controller.hanlder.BusinessException;
import com.brecycle.entity.*;
import com.brecycle.entity.dto.CustomerRegistParam;
import com.brecycle.entity.dto.EntRegistParam;
import com.brecycle.entity.dto.UserInfo;
import com.brecycle.enums.AccessStatus;
import com.brecycle.enums.RoleEnums;
import com.brecycle.enums.UserStatus;
import com.brecycle.enums.UserType;
import com.brecycle.mapper.*;
import com.brecycle.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户模块块
 *
 * @author cmgun
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private EntInfoMapper entInfoMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private FiscoBcos fiscoBcos;

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

        // 设置jwt token
        long currentTimeMillis = System.currentTimeMillis();
        String token = JwtTokenUtil.generateToken(user.getUserName(), currentTimeMillis);
        redisUtil.setCacheObject(RedisConstant.CACHE_PREFIX + RedisConstant.USER_TOKEN_KEY + user.getUserName()
                , currentTimeMillis, JWTConfig.expiration + JWTConfig.redisExpiration, TimeUnit.SECONDS);

        return UserInfo.builder()
                .userName(user.getUserName())
                .token(token)
//                .role(roleList.stream().map(Role::getKey).collect(Collectors.toList()))
//                .resources(resourceList)
                .build();
    }

    @Override
    public UserInfo getInfo(HttpServletRequest request) {
        String token = request.getHeader(JWTConfig.tokenHeader);
        String userName = JwtTokenUtil.getUsername(token);
        User user = userMapper.selectByUserName(userName);
        // 查找角色和菜单资源
        List<Role> roleList = roleMapper.selectByUserId(user.getId());
        List<Resource> resourceList = resourceMapper.selectByRoleIds(roleList.stream().map(Role::getId).collect(Collectors.toList()));
        return UserInfo.builder()
                .userName(user.getUserName())
                .token(token)
                .roles(roleList.stream().map(Role::getKey).collect(Collectors.toList()))
                .resources(resourceList)
                .build();
    }

    @Override
    public void logout(String userName) {
        redisUtil.deleteObject(RedisConstant.CACHE_PREFIX + RedisConstant.USER_TOKEN_KEY + userName);
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void customerRegist(CustomerRegistParam param) {
        // 查询是否已有重复账户
        User existsUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, param.getUserName()));
        if (existsUser != null) {
            throw new BusinessException("账户重复");
        }
        existsUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getIdno, param.getIdno()));
        if (existsUser != null) {
            throw new BusinessException("身份信息重复");
        }

        // 创建非国密账户
        // 创建非国密类型的CryptoSuite
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // 随机生成非国密公私钥对
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        // 获取账户地址
        String accountAddress = cryptoKeyPair.getAddress();
        String privateKey = cryptoKeyPair.getHexPrivateKey();

        User entity = new User();
        entity.setUserName(param.getUserName());
        entity.setPassword(param.getPassword());
        entity.setStatus(UserStatus.NORMAL.getValue());
        entity.setName(param.getName());
        entity.setMobile(param.getPhone());
        entity.setIdno(param.getIdno());
        entity.setAddr(accountAddress);
        entity.setPrivateKey(privateKey);
        entity.setUserType(UserType.CUSTOMER.getValue());
        userMapper.insert(entity);
        // 关联角色
        UserRole userRole = new UserRole();
        userRole.setUserId(entity.getId());
        userRole.setRoleId(Long.valueOf(RoleEnums.CUSTOMER.getKey()));
        userRoleMapper.insert(userRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void entRegist(EntRegistParam param) {
        // 查询是否已有重复账户
        User existsUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, param.getUserName()));
        if (existsUser != null) {
            throw new BusinessException("账户重复");
        }
        existsUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getIdno, param.getIdno()));
        if (existsUser != null) {
            throw new BusinessException("企业信息重复");
        }

        // 创建非国密账户
        // 创建非国密类型的CryptoSuite
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // 随机生成非国密公私钥对
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        // 获取账户地址
        String accountAddress = cryptoKeyPair.getAddress();
        String privateKey = cryptoKeyPair.getHexPrivateKey();

        User entity = new User();
        entity.setUserName(param.getUserName());
        entity.setPassword(param.getPassword());
        entity.setStatus(UserStatus.NORMAL.getValue());
        entity.setName(param.getName());
        entity.setMobile(param.getPhone());
        entity.setIdno(param.getIdno());
        entity.setAddr(accountAddress);
        entity.setPrivateKey(privateKey);
        entity.setUserType(UserType.ENTERPRISE.getValue());
        entity.setAddress(param.getAddress());

        userMapper.insert(entity);
        // 关联角色
        UserRole userRole = new UserRole();
        userRole.setUserId(entity.getId());
        // 回收商特有的审批中角色，控制资源
        userRole.setRoleId(Integer.valueOf(param.getType()).equals(RoleEnums.RECYCLE.getKey())
                ? Long.valueOf(RoleEnums.RECYCLE_AUDIT.getKey()) : Long.valueOf(param.getType()));
        userRoleMapper.insert(userRole);
        // 关联企业信息
        EntInfo entInfo = new EntInfo();
        entInfo.setUserId(entity.getId());
        entInfo.setAccessStatus(Integer.valueOf(param.getType()).equals(RoleEnums.RECYCLE.getKey())
                ? AccessStatus.WAIT_APPLY.getValue() : AccessStatus.DEFAULT.getValue());
        entInfoMapper.insert(entInfo);
    }
}
