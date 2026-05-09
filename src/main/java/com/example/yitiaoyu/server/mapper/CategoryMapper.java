package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    Category selectById(Long id);

    void insert(Category category);

    void updateById(Category category);

    void deleteById(Long id);

    List<Category> selectAll();

    Integer selectCount();
}
