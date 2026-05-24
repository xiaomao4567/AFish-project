package com.example.yitiaoyu.server.service;

import com.example.yitiaoyu.pojo.dto.OrderDTO;
import com.example.yitiaoyu.pojo.vo.OrderVO;
import com.example.yitiaoyu.pojo.vo.PageVO;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderVO create(OrderDTO orderDTO);
    PageVO<OrderVO> list(String status, LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size);
    OrderVO getById(Long id);
    void updateStatus(Long id, String newStatus, String role);
    List<OrderVO> listByTable(Integer tableNumber);
    List<OrderVO> listByUserIdAndTableNumber(Long userId, Integer tableNumber);
    void cancelOrder(Long id, String cancelReason, String role);
}