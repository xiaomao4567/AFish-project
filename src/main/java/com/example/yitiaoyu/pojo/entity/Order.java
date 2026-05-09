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
public class Order {


    private Long id;
    private String orderNo;
    private Integer tableNumber;
    private BigDecimal totalAmount;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime prepareTime;
    private LocalDateTime finishTime;
    private LocalDateTime updateTime;
}