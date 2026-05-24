package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.common.UserContext;
import com.example.yitiaoyu.pojo.dto.OrderDTO;
import com.example.yitiaoyu.pojo.dto.OrderItemDTO;
import com.example.yitiaoyu.pojo.entity.*;
import com.example.yitiaoyu.pojo.vo.OrderItemVO;
import com.example.yitiaoyu.pojo.vo.OrderVO;
import com.example.yitiaoyu.pojo.vo.PageVO;
import com.example.yitiaoyu.server.mapper.*;
import com.example.yitiaoyu.server.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    @Transactional
    public OrderVO create(OrderDTO orderDTO) {
        log.info("【订单创建】用户ID: {} 创建订单 - tableNumber: {}", orderDTO.getUserId(), orderDTO.getTableNumber());
        
        List<OrderItemDTO> items = orderDTO.getItems();
        if (items == null || items.isEmpty()) {
            log.warn("【订单创建】购物车为空 - tableNumber: {}", orderDTO.getTableNumber());
            throw new BusinessException("购物车为空");
        }
        
        String orderNo = generateOrderNo();
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setTableNumber(orderDTO.getTableNumber());
        order.setTotalAmount(totalAmount);
        order.setStatus("PAID");
        order.setRemark(orderDTO.getRemark());
        order.setUserId(orderDTO.getUserId());
        order.setCreateTime(LocalDateTime.now());
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);
        
        log.info("【订单创建】订单创建成功 - orderId: {}, orderNo: {}, totalAmount: {}", 
                order.getId(), orderNo, totalAmount);
        
        for (OrderItemDTO item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setDishId(item.getDishId());
            orderItem.setDishName(item.getDishName());
            orderItem.setImage(item.getImage());
            orderItem.setFlavor(item.getFlavor());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());
            orderItem.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(orderItem);
        }
        
        log.info("【订单创建】订单创建完成 - orderId: {}", order.getId());
        return getById(order.getId());
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long id, String cancelReason, String role) {
        log.info("【订单取消】用户: {} 取消订单 - orderId: {}, cancelReason: {}, role: {}", 
                UserContext.getUsername(), id, cancelReason, role);
        
        Order order = orderMapper.selectById(id);
        if (order == null) {
            log.warn("【订单取消】订单不存在 - orderId: {}", id);
            throw new BusinessException("订单不存在");
        }
        
        String currentStatus = order.getStatus();
        if (!("PAID".equals(currentStatus) || "PREPARING".equals(currentStatus))) {
            log.warn("【订单取消】订单状态不允许取消 - orderId: {}, currentStatus: {}", id, currentStatus);
            throw new BusinessException("当前订单状态不允许取消");
        }
        
        order.setStatus("CANCELLED");
        order.setCancelReason(cancelReason);
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("【订单取消】订单取消成功 - orderId: {}", id);
    }

    @Override
    public PageVO<OrderVO> list(String status, LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size) {
        log.info("【订单查询】用户: {} 查询订单列表 - status: {}, startTime: {}, endTime: {}, page: {}, size: {}", 
                UserContext.getUsername(), status, startTime, endTime, page, size);
        List<Order> orders;
        if (status != null && !status.isEmpty() && startTime != null && endTime != null) {
            orders = orderMapper.selectByStatusAndTimeRange(status, startTime, endTime);
        } else if (status != null && !status.isEmpty() && startTime != null) {
            orders = orderMapper.selectByStatus(status).stream()
                    .filter(o -> o.getCreateTime().isAfter(startTime) || o.getCreateTime().isEqual(startTime))
                    .collect(Collectors.toList());
        } else if (status != null && !status.isEmpty() && endTime != null) {
            orders = orderMapper.selectByStatus(status).stream()
                    .filter(o -> o.getCreateTime().isBefore(endTime) || o.getCreateTime().isEqual(endTime))
                    .collect(Collectors.toList());
        } else if (startTime != null && endTime != null) {
            orders = orderMapper.selectAllByTimeRange(startTime, endTime);
        } else if (status != null && !status.isEmpty()) {
            orders = orderMapper.selectByStatus(status);
        } else if (startTime != null) {
            orders = orderMapper.selectByStartTime(startTime);
        } else if (endTime != null) {
            orders = orderMapper.selectByEndTime(endTime);
        } else {
            orders = orderMapper.selectAll();
        }
        log.info("【订单查询】查询到订单数量: {}", orders.size());
        int start = (page - 1) * size;
        int end = Math.min(start + size, orders.size());
        List<Order> pageContent = start < orders.size() ? orders.subList(start, end) : List.of();
        return new PageVO<>(
                pageContent.stream().map(this::convertToVO).toList(),
                (long) orders.size(),
                page,
                size
        );
    }

    @Override
    public OrderVO getById(Long id) {
        log.info("【订单查询】用户: {} 查询订单详情 - orderId: {}", UserContext.getUsername(), id);
        Order order = orderMapper.selectById(id);
        if (order == null) {
            log.warn("【订单查询】订单不存在 - orderId: {}", id);
            throw new BusinessException("订单不存在");
        }
        return convertToVO(order);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String newStatus, String role) {
        log.info("【订单状态更新】用户: {} 更新订单状态 - orderId: {}, newStatus: {}, role: {}", 
                UserContext.getUsername(), id, newStatus, role);
        Order order = orderMapper.selectById(id);
        if (order == null) {
            log.warn("【订单状态更新】订单不存在 - orderId: {}", id);
            throw new BusinessException("订单不存在");
        }
        String currentStatus = order.getStatus();
        
        if ("PAID".equals(currentStatus) && "PREPARING".equals(newStatus)) {
            order.setStatus(newStatus);
            order.setPrepareTime(LocalDateTime.now());
        } else if ("PREPARING".equals(currentStatus) && "SERVED".equals(newStatus)) {
            order.setStatus(newStatus);
            order.setFinishTime(LocalDateTime.now());
        } else {
            log.warn("【订单状态更新】无效的状态变更 - currentStatus: {}, newStatus: {}", currentStatus, newStatus);
            throw new BusinessException("无效的状态变更");
        }
        
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        log.info("【订单状态更新】订单状态更新成功 - orderId: {}, status: {}", id, newStatus);
    }

    @Override
    public List<OrderVO> listByTable(Integer tableNumber) {
        log.info("【订单查询】查询桌号订单 - tableNumber: {}", tableNumber);
        List<Order> orders = orderMapper.selectByTableNumber(tableNumber);
        log.info("【订单查询】查询到桌号订单数量: {}", orders.size());
        return orders.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<OrderVO> listByUserIdAndTableNumber(Long userId, Integer tableNumber) {
        log.info("【订单查询】查询用户订单 - userId: {}, tableNumber: {}", userId, tableNumber);
        List<Order> orders = orderMapper.selectByUserIdAndTableNumber(userId, tableNumber);
        log.info("【订单查询】查询到用户订单数量: {}", orders.size());
        return orders.stream().map(this::convertToVO).toList();
    }

    private String generateOrderNo() {
        return "YC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                String.format("%04d", (int) (Math.random() * 10000));
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTableNumber(order.getTableNumber());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setRemark(order.getRemark());
        vo.setCancelReason(order.getCancelReason());
        vo.setUserId(order.getUserId());
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        vo.setPrepareTime(order.getPrepareTime());
        vo.setFinishTime(order.getFinishTime());
        vo.setCancelTime(order.getCancelTime());
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        vo.setItems(items.stream().map(this::convertItemToVO).toList());
        return vo;
    }

    private OrderItemVO convertItemToVO(OrderItem item) {
        OrderItemVO vo = new OrderItemVO();
        vo.setId(item.getId());
        vo.setDishId(item.getDishId());
        vo.setDishName(item.getDishName());
        vo.setImage(item.getImage());
        vo.setFlavor(item.getFlavor());
        vo.setQuantity(item.getQuantity());
        vo.setPrice(item.getPrice());
        vo.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return vo;
    }
}
