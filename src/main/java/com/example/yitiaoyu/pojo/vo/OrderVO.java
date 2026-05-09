package com.example.yitiaoyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderVO {
    private Long id;
    private String orderNo;
    private Integer tableNumber;
    private BigDecimal totalAmount;
    private String status;
    private String remark;
    private List<OrderItemVO> items;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime prepareTime;
    private LocalDateTime finishTime;
}