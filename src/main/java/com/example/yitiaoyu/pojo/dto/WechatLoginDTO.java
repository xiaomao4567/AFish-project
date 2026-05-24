package com.example.yitiaoyu.pojo.dto;

import lombok.Data;

@Data
public class WechatLoginDTO {
    private String code;
    private String nickName;
    private String avatarUrl;
}