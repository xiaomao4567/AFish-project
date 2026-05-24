package com.example.yitiaoyu.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WechatUser {
    private Long id;
    private String openId;
    private String nickname;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}