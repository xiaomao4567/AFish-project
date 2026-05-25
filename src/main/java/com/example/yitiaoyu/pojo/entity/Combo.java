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
public class Combo {


    private Long id;
    private String name;
    private String image;
    private String description;
    private BigDecimal price;
    private Integer status;
    private Integer recommendIndex;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}