package com.example.yitiaoyu.pojo.entity;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {


    private Long id;
    private String username;
    private String password;
    private String realName;
    private String role;
    private String phone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}