package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    Order selectById(Long id);

    void insert(Order order);

    void updateById(Order order);

    void deleteById(Long id);

    List<Order> selectByStatusAndTimeRange(String status, LocalDateTime startTime, LocalDateTime endTime);

    List<Order> selectByTableNumber(Integer tableNumber);

    BigDecimal sumTotalAmountByStatusAndTimeRange(String status, LocalDateTime startTime, LocalDateTime endTime);

    Integer countByStatusAndTimeRange(String status, LocalDateTime startTime, LocalDateTime endTime);

    List<Order> selectAllByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    List<Order> selectByStatus(String status);

    List<Order> selectAll();

    List<Order> selectByStartTime(LocalDateTime startTime);

    List<Order> selectByEndTime(LocalDateTime endTime);
}
