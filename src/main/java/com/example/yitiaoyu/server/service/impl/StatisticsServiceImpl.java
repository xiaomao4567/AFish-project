package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.pojo.entity.Order;
import com.example.yitiaoyu.pojo.vo.DailyStatistics;
import com.example.yitiaoyu.pojo.vo.StatisticsVO;
import com.example.yitiaoyu.server.mapper.OrderMapper;
import com.example.yitiaoyu.server.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderMapper orderMapper;

    public StatisticsServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public StatisticsVO getStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        StatisticsVO vo = new StatisticsVO();
        BigDecimal paidAmount = orderMapper.sumTotalAmountByStatusAndTimeRange("已支付", startTime, endTime);
        BigDecimal finishedAmount = orderMapper.sumTotalAmountByStatusAndTimeRange("已出餐", startTime, endTime);
        vo.setTotalAmount(paidAmount != null ? paidAmount.add(finishedAmount != null ? finishedAmount : BigDecimal.ZERO) : BigDecimal.ZERO);
        Integer paidCount = orderMapper.countByStatusAndTimeRange("已支付", startTime, endTime);
        Integer finishedCount = orderMapper.countByStatusAndTimeRange("已出餐", startTime, endTime);
        vo.setOrderCount((paidCount != null ? paidCount : 0) + (finishedCount != null ? finishedCount : 0));
        if (vo.getOrderCount() > 0) {
            vo.setAvgAmount(vo.getTotalAmount().divide(BigDecimal.valueOf(vo.getOrderCount()), 2, RoundingMode.HALF_UP));
        } else {
            vo.setAvgAmount(BigDecimal.ZERO);
        }
        vo.setDailyList(getDailyStatistics(startTime, endTime));
        return vo;
    }

    private List<DailyStatistics> getDailyStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<DailyStatistics> list = new ArrayList<>();
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();
        while (!startDate.isAfter(endDate)) {
            LocalDateTime dayStart = LocalDateTime.of(startDate, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(startDate, LocalTime.MAX);
            BigDecimal amount = orderMapper.sumTotalAmountByStatusAndTimeRange("已支付", dayStart, dayEnd);
            BigDecimal finishedAmount = orderMapper.sumTotalAmountByStatusAndTimeRange("已出餐", dayStart, dayEnd);
            DailyStatistics daily = new DailyStatistics();
            daily.setDate(startDate.toString());
            daily.setAmount(amount != null ? amount.add(finishedAmount != null ? finishedAmount : BigDecimal.ZERO) : BigDecimal.ZERO);
            list.add(daily);
            startDate = startDate.plusDays(1);
        }
        return list;
    }
}