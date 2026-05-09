package com.example.yitiaoyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeVO {
    private Long id;
    private String username;
    private String realName;
    private String role;
    private String phone;
    private LocalDateTime createTime;
}