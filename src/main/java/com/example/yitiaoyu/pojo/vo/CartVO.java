package com.example.yitiaoyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartVO {
    private Integer tableNumber;
    private List<CartItemVO> items;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
}