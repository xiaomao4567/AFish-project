package com.example.yitiaoyu.pojo.vo;

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
public class DishVO {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String image;
    private String description;
    private String[] flavors;
    private BigDecimal price;
    private Integer status;
    private LocalDateTime createTime;
}