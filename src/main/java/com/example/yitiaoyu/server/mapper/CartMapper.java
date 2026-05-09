package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.Cart;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CartMapper {

    @Select("SELECT * FROM cart WHERE id = #{id}")
    Cart selectById(Long id);

    @Insert("INSERT INTO cart (table_number, create_time, update_time) VALUES (#{tableNumber}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Cart cart);

    @Update("UPDATE cart SET table_number = #{tableNumber}, update_time = #{updateTime} WHERE id = #{id}")
    void updateById(Cart cart);

    @Delete("DELETE FROM cart WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM cart WHERE table_number = #{tableNumber}")
    Cart selectByTableNumber(@Param("tableNumber") Integer tableNumber);
}