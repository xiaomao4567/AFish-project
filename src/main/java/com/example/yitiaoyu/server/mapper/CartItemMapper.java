package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartItemMapper {

    CartItem selectById(Long id);

    void insert(CartItem cartItem);

    void updateById(CartItem cartItem);

    void deleteById(Long id);

    List<CartItem> selectByCartId(Long cartId);

    void deleteByCartId(Long cartId);

    CartItem selectByCartIdAndDishIdAndFlavor(Long cartId, Long dishId, String flavor);
}
