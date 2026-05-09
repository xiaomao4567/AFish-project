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
public class StatisticsVO {
    private BigDecimal totalAmount;
    private Integer orderCount;
    private BigDecimal avgAmount;
    private List<DailyStatistics> dailyList;
}