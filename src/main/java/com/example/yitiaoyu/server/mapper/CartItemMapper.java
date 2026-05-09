package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.CartItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartItemMapper {

    @Select("SELECT * FROM cart_item WHERE id = #{id}")
    CartItem selectById(Long id);

    @Insert("INSERT INTO cart_item (cart_id, dish_id, flavor, quantity, price, create_time, update_time) VALUES (#{cartId}, #{dishId}, #{flavor}, #{quantity}, #{price}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CartItem cartItem);

    @Update("UPDATE cart_item SET cart_id = #{cartId}, dish_id = #{dishId}, flavor = #{flavor}, quantity = #{quantity}, price = #{price}, update_time = #{updateTime} WHERE id = #{id}")
    void updateById(CartItem cartItem);

    @Delete("DELETE FROM cart_item WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM cart_item WHERE cart_id = #{cartId}")
    List<CartItem> selectByCartId(@Param("cartId") Long cartId);

    @Delete("DELETE FROM cart_item WHERE cart_id = #{cartId}")
    void deleteByCartId(@Param("cartId") Long cartId);

    @Select("SELECT * FROM cart_item WHERE cart_id = #{cartId} AND dish_id = #{dishId} AND flavor = #{flavor}")
    CartItem selectByCartIdAndDishIdAndFlavor(@Param("cartId") Long cartId, @Param("dishId") Long dishId, @Param("flavor") String flavor);
}