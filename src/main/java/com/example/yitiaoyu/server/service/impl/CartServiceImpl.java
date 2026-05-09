package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.common.UserContext;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public CartVO getByTable(Integer tableNumber) {
        log.info("【购物车查询】用户: {} 查询桌号购物车 - tableNumber: {}", UserContext.getUsername(), tableNumber);
        Cart cart = cartMapper.selectByTableNumber(tableNumber);
        if (cart == null) {
            log.info("【购物车查询】桌号购物车不存在 - tableNumber: {}", tableNumber);
            CartVO vo = new CartVO();
            vo.setTableNumber(tableNumber);
            vo.setItems(List.of());
            vo.setTotalQuantity(0);
            vo.setTotalAmount(BigDecimal.ZERO);
            return vo;
        }
        List<CartItem> items = cartItemMapper.selectByCartId(cart.getId());
        log.info("【购物车查询】查询到购物车项数量: {}", items.size());
        return convertToVO(cart, items);
    }

    @Override
    @Transactional
    public void addItem(Integer tableNumber, CartItemDTO cartItemDTO) {
        log.info("【购物车添加】用户: {} 添加商品到购物车 - tableNumber: {}, dishId: {}, flavor: {}, quantity: {}", 
                UserContext.getUsername(), tableNumber, cartItemDTO.getDishId(), cartItemDTO.getFlavor(), cartItemDTO.getQuantity());
        Dish dish = dishMapper.selectById(cartItemDTO.getDishId());
        if (dish == null || dish.getStatus() != 1) {
            log.warn("【购物车添加】菜品不存在或未上架 - dishId: {}", cartItemDTO.getDishId());
            throw new BusinessException("菜品不存在或未上架");
        }
        Cart cart = cartMapper.selectByTableNumber(tableNumber);
        if (cart == null) {
            log.info("【购物车添加】创建新购物车 - tableNumber: {}", tableNumber);
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
            log.info("【购物车添加】更新购物车项数量 - itemId: {}, quantity: {}", existingItem.getId(), existingItem.getQuantity());
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
            log.info("【购物车添加】添加新购物车项 - itemId: {}", item.getId());
        }
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.updateById(cart);
    }

    @Override
    @Transactional
    public void updateItem(Long itemId, Integer quantity) {
        log.info("【购物车更新】用户: {} 更新购物车项数量 - itemId: {}, quantity: {}", UserContext.getUsername(), itemId, quantity);
        CartItem item = cartItemMapper.selectById(itemId);
        if (item == null) {
            log.warn("【购物车更新】购物车项不存在 - itemId: {}", itemId);
            throw new BusinessException("购物车项不存在");
        }
        if (quantity <= 0) {
            cartItemMapper.deleteById(itemId);
            log.info("【购物车更新】删除购物车项 - itemId: {}", itemId);
        } else {
            item.setQuantity(quantity);
            item.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateById(item);
            log.info("【购物车更新】更新成功 - itemId: {}, quantity: {}", itemId, quantity);
        }
    }

    @Override
    @Transactional
    public void removeItem(Long itemId) {
        log.info("【购物车删除】用户: {} 删除购物车项 - itemId: {}", UserContext.getUsername(), itemId);
        if (cartItemMapper.selectById(itemId) == null) {
            log.warn("【购物车删除】购物车项不存在 - itemId: {}", itemId);
            throw new BusinessException("购物车项不存在");
        }
        cartItemMapper.deleteById(itemId);
        log.info("【购物车删除】删除成功 - itemId: {}", itemId);
    }

    @Override
    @Transactional
    public void clear(Integer tableNumber) {
        log.info("【购物车清空】用户: {} 清空桌号购物车 - tableNumber: {}", UserContext.getUsername(), tableNumber);
        Cart cart = cartMapper.selectByTableNumber(tableNumber);
        if (cart != null) {
            cartItemMapper.deleteByCartId(cart.getId());
            cartMapper.deleteById(cart.getId());
            log.info("【购物车清空】清空成功 - tableNumber: {}", tableNumber);
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
