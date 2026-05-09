package com.example.yitiaoyu.pojo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dish {

    private Long id;
    private String name;
    private Long categoryId;
    private String image;
    private String description;
    private String flavors;
    private BigDecimal price;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}