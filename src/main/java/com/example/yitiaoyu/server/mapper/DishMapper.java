package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishMapper {

    Dish selectById(Long id);

    void insert(Dish dish);

    void updateById(Dish dish);

    void deleteById(Long id);

    List<Dish> selectByCategory(Long categoryId);

    Integer countByCategory(Long categoryId);

    List<Dish> selectByCategoryAll(Long categoryId);

    List<Dish> selectByName(String name);

    List<Dish> selectAll();

    List<Dish> selectByCategoryAndName(@Param("categoryId") Long categoryId, @Param("name") String name);
}