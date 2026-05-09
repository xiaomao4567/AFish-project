package com.example.yitiaoyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemVO {
    private Long id;
    private Long dishId;
    private String dishName;
    private String image;
    private String flavor;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}