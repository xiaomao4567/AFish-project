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
public class OrderItem {

    private Long id;
    private Long orderId;
    private Long dishId;
    private String dishName;
    private String image;
    private String flavor;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime createTime;
}