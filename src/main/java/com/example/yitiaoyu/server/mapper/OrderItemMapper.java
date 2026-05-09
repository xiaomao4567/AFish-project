package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    OrderItem selectById(Long id);

    void insert(OrderItem orderItem);

    void updateById(OrderItem orderItem);

    void deleteById(Long id);

    List<OrderItem> selectByOrderId(Long orderId);

    void deleteByOrderId(Long orderId);
}
