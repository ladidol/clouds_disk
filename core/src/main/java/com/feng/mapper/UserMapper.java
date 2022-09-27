package com.feng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.feng.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ladidol
 * @date: 2022/9/27 23:13
 * @description:
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
