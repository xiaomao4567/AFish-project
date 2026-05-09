package com.example.yitiaoyu.server.service;

import com.example.yitiaoyu.pojo.dto.CartItemDTO;
import com.example.yitiaoyu.pojo.vo.CartVO;

public interface CartService {
    CartVO getByTable(Integer tableNumber);
    void addItem(Integer tableNumber, CartItemDTO cartItemDTO);
    void updateItem(Long itemId, Integer quantity);
    void removeItem(Long itemId);
    void clear(Integer tableNumber);
}