package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.WechatUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WechatUserMapper {
    WechatUser selectByOpenId(@Param("openId") String openId);
    
    int insert(WechatUser wechatUser);
    
    int updateByOpenId(WechatUser wechatUser);
}