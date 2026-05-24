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
public class OrderItemDTO {
    private Long dishId;
    private String dishName;
    private String image;
    private String flavor;
    private Integer quantity;
    private BigDecimal price;
    private Boolean isCombo;
}