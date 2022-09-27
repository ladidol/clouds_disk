package com.feng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.feng.entity.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper extends BaseMapper<User> {

}
