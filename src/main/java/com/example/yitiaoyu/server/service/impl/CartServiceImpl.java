package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.pojo.dto.CartItemDTO;
import com.example.yitiaoyu.pojo.entity.Cart;
import com.example.yitiaoyu.pojo.entity.CartItem;
import com.example.yitiaoyu.pojo.entity.Dish;
import com.example.yitiaoyu.pojo.vo.CartItemVO;
import com.example.yitiaoyu.pojo.vo.CartVO;
import com.example.yitiaoyu.server.mapper.CartItemMapper;
import com.example.yitiaoyu.server.mapper.CartMapper;
import com.example.yitiaoyu.server.mapper.DishMapper;
import com.example.yitiaoyu.server.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final DishMapper dishMapper;

    public CartServiceImpl(CartMapper cartMapper, CartItemMapper cartItemMapper, DishMapper dishMapper) {
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
        this.dishMapper = dishMapper;
    }

    @Override
    public CartVO getByTable(Integer tableNumber) {
        Cart cart = cartMapper.selectByTableNumber(tableNumber);
        if (cart == null) {
            CartVO vo = new CartVO();
            vo.setTableNumber(tableNumber);
            vo.setItems(List.of());
            vo.setTotalQuantity(0);
            vo.setTotalAmount(BigDecimal.ZERO);
            return vo;
        }
        List<CartItem> items = cartItemMapper.selectByCartId(cart.getId());
        return convertToVO(cart, items);
    }

    @Override
    @Transactional
    public void addItem(Integer tableNumber, CartItemDTO cartItemDTO) {
        Dish dish = dishMapper.selectById(cartItemDTO.getDishId());
        if (dish == null || dish.getStatus() != 1) {
            throw new BusinessException("菜品不存在或未上架");
        }
        Cart cart = cartMapper.selectByTableNumber(tableNumber);
        if (cart == null) {
            cart = new Cart();
            cart.setTableNumber(tableNumber);
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            cartMapper.insert(cart);
        }
        CartItem existingItem = cartItemMapper.selectByCartIdAndDishIdAndFlavor(cart.getId(), cartItemDTO.getDishId(), cartItemDTO.getFlavor());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + cartItemDTO.getQuantity());
            existingItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateById(existingItem);
        } else {
            CartItem item = new CartItem();
            item.setCartId(cart.getId());
            item.setDishId(cartItemDTO.getDishId());
            item.setFlavor(cartItemDTO.getFlavor());
            item.setQuantity(cartItemDTO.getQuantity());
            item.setPrice(dish.getPrice());
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            cartItemMapper.insert(item);
        }
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.updateById(cart);
    }

    @Override
    @Transactional
    public void updateItem(Long itemId, Integer quantity) {
        CartItem item = cartItemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("购物车项不存在");
        }
        if (quantity <= 0) {
            cartItemMapper.deleteById(itemId);
        } else {
            item.setQuantity(quantity);
            item.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateById(item);
        }
    }

    @Override
    @Transactional
    public void removeItem(Long itemId) {
        if (cartItemMapper.selectById(itemId) == null) {
            throw new BusinessException("购物车项不存在");
        }
        cartItemMapper.deleteById(itemId);
    }

    @Override
    @Transactional
    public void clear(Integer tableNumber) {
        Cart cart = cartMapper.selectByTableNumber(tableNumber);
        if (cart != null) {
            cartItemMapper.deleteByCartId(cart.getId());
            cartMapper.deleteById(cart.getId());
        }
    }

    private CartVO convertToVO(Cart cart, List<CartItem> items) {
        CartVO vo = new CartVO();
        vo.setTableNumber(cart.getTableNumber());
        vo.setItems(items.stream().map(this::convertItemToVO).toList());
        vo.setTotalQuantity(items.stream().mapToInt(CartItem::getQuantity).sum());
        vo.setTotalAmount(items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return vo;
    }

    private CartItemVO convertItemToVO(CartItem item) {
        CartItemVO vo = new CartItemVO();
        vo.setId(item.getId());
        vo.setDishId(item.getDishId());
        Dish dish = dishMapper.selectById(item.getDishId());
        if (dish != null) {
            vo.setDishName(dish.getName());
            vo.setImage(dish.getImage());
        }
        vo.setFlavor(item.getFlavor());
        vo.setQuantity(item.getQuantity());
        vo.setPrice(item.getPrice());
        vo.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return vo;
    }
}