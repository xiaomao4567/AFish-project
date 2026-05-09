package com.example.yitiaoyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginVO {
    private Long id;
    private String username;
    private String realName;
    private String role;
    private String token;
}