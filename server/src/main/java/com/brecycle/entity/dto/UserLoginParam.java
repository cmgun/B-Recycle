package com.brecycle.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录实体
 *
 * @author cmgun
 */
@Data
@Builder
public class UserLoginParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    private String password;
}
