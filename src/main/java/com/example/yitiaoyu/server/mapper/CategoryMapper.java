package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category selectById(Long id);

    @Insert("INSERT INTO category (name, sort_order, create_time, update_time) VALUES (#{name}, #{sortOrder}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    @Update("UPDATE category SET name = #{name}, sort_order = #{sortOrder}, update_time = #{updateTime} WHERE id = #{id}")
    void updateById(Category category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM category ORDER BY sort_order ASC")
    List<Category> selectAll();

    @Select("SELECT COUNT(*) FROM category")
    Integer selectCount();
}