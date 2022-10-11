package com.brecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.brecycle.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author cmgun
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByUserName(@Param("name") String name);
}
