package com.example.yitiaoyu.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DishDTO {
    private String name;
    private Long categoryId;
    private String image;
    private String description;
    private String flavors;
    private BigDecimal price;
    private Integer status;
}