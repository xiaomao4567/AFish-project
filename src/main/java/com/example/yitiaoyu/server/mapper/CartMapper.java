package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper {

    Cart selectById(Long id);

    void insert(Cart cart);

    void updateById(Cart cart);

    void deleteById(Long id);

    Cart selectByTableNumber(Integer tableNumber);
}
