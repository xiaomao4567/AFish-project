package com.example.yitiaoyu.server.service;

import com.example.yitiaoyu.pojo.dto.CategoryDTO;
import com.example.yitiaoyu.pojo.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> list();
    Category getById(Long id);
    void create(CategoryDTO categoryDTO);
    void update(Long id, CategoryDTO categoryDTO);
    void delete(Long id);
}