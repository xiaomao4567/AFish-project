package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.ComboItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ComboItemMapper {

    @Select("SELECT * FROM combo_item WHERE id = #{id}")
    ComboItem selectById(Long id);

    @Insert("INSERT INTO combo_item (combo_id, dish_id, flavor, quantity, create_time) VALUES (#{comboId}, #{dishId}, #{flavor}, #{quantity}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ComboItem comboItem);

    @Update("UPDATE combo_item SET combo_id = #{comboId}, dish_id = #{dishId}, flavor = #{flavor}, quantity = #{quantity} WHERE id = #{id}")
    void updateById(ComboItem comboItem);

    @Delete("DELETE FROM combo_item WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM combo_item WHERE combo_id = #{comboId}")
    List<ComboItem> selectByComboId(@Param("comboId") Long comboId);

    @Delete("DELETE FROM combo_item WHERE combo_id = #{comboId}")
    void deleteByComboId(@Param("comboId") Long comboId);
}