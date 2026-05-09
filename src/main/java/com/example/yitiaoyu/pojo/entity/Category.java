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
public class Category {


    private Long id;
    private String name;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}