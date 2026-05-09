package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Select("SELECT * FROM order_item WHERE id = #{id}")
    OrderItem selectById(Long id);

    @Insert("INSERT INTO order_item (order_id, dish_id, dish_name, image, flavor, quantity, price, create_time) VALUES (#{orderId}, #{dishId}, #{dishName}, #{image}, #{flavor}, #{quantity}, #{price}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OrderItem orderItem);

    @Update("UPDATE order_item SET order_id = #{orderId}, dish_id = #{dishId}, dish_name = #{dishName}, image = #{image}, flavor = #{flavor}, quantity = #{quantity}, price = #{price} WHERE id = #{id}")
    void updateById(OrderItem orderItem);

    @Delete("DELETE FROM order_item WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

    @Delete("DELETE FROM order_item WHERE order_id = #{orderId}")
    void deleteByOrderId(@Param("orderId") Long orderId);
}