package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectByOpenId(String openId);
    int insert(User user);
    int updateByOpenId(User user);
    User selectById(Long id);
}