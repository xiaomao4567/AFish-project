package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.pojo.dto.OrderDTO;
import com.example.yitiaoyu.pojo.entity.*;
import com.example.yitiaoyu.pojo.vo.OrderItemVO;
import com.example.yitiaoyu.pojo.vo.OrderVO;
import com.example.yitiaoyu.pojo.vo.PageVO;
import com.example.yitiaoyu.server.mapper.*;
import com.example.yitiaoyu.server.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final DishMapper dishMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                           CartMapper cartMapper, CartItemMapper cartItemMapper, DishMapper dishMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
        this.dishMapper = dishMapper;
    }

    @Override
    @Transactional
    public OrderVO create(OrderDTO orderDTO) {
        Cart cart = cartMapper.selectByTableNumber(orderDTO.getTableNumber());
        if (cart == null) {
            throw new BusinessException("购物车为空");
        }
        List<CartItem> cartItems = cartItemMapper.selectByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空");
        }
        String orderNo = generateOrderNo();
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setTableNumber(orderDTO.getTableNumber());
        order.setTotalAmount(totalAmount);
        order.setStatus("已支付");
        order.setRemark(orderDTO.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setDishId(cartItem.getDishId());
            Dish dish = dishMapper.selectById(cartItem.getDishId());
            if (dish != null) {
                orderItem.setDishName(dish.getName());
                orderItem.setImage(dish.getImage());
            }
            orderItem.setFlavor(cartItem.getFlavor());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(orderItem);
        }
        cartItemMapper.deleteByCartId(cart.getId());
        cartMapper.deleteById(cart.getId());
        return getById(order.getId());
    }

    @Override
    public PageVO<OrderVO> list(String status, LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size) {
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
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToVO(order);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String newStatus, String role) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        String currentStatus = order.getStatus();
        if ("ADMIN".equals(role)) {
            if ("已支付".equals(currentStatus) && "待出餐".equals(newStatus)) {
                order.setStatus(newStatus);
                order.setPrepareTime(LocalDateTime.now());
            } else if ("待出餐".equals(currentStatus) && "已出餐".equals(newStatus)) {
                order.setStatus(newStatus);
                order.setFinishTime(LocalDateTime.now());
            } else {
                throw new BusinessException("无效的状态变更");
            }
        } else {
            if (!("已支付".equals(currentStatus) && "待出餐".equals(newStatus)) &&
                    !("待出餐".equals(currentStatus) && "已出餐".equals(newStatus))) {
                throw new BusinessException("无权限进行此状态变更");
            }
            order.setStatus(newStatus);
            if ("待出餐".equals(newStatus)) {
                order.setPrepareTime(LocalDateTime.now());
            } else if ("已出餐".equals(newStatus)) {
                order.setFinishTime(LocalDateTime.now());
            }
        }
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public List<OrderVO> listByTable(Integer tableNumber) {
        List<Order> orders = orderMapper.selectByTableNumber(tableNumber);
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
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        vo.setPrepareTime(order.getPrepareTime());
        vo.setFinishTime(order.getFinishTime());
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