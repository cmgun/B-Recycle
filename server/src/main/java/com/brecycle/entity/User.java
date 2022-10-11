package com.brecycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户类实体
 *
 * @author cmgun
 */
@Data
@TableName("user")
public class User {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField
    private String name;

    /**
     * 密码
     */
    @TableField
    private String password;

    /**
     * 公钥
     */
    @TableField
    private String publicKey;

    /**
     * weId
     */
    @TableField
    private String weId;
}
