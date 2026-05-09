package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.pojo.dto.CategoryDTO;
import com.example.yitiaoyu.pojo.entity.Category;
import com.example.yitiaoyu.server.mapper.CategoryMapper;
import com.example.yitiaoyu.server.mapper.DishMapper;
import com.example.yitiaoyu.server.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final DishMapper dishMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, DishMapper dishMapper) {
        this.categoryMapper = categoryMapper;
        this.dishMapper = dishMapper;
    }

    @Override
    public List<Category> list() {
        log.info("查询分类列表");
        List<Category> categories = categoryMapper.selectAll();
        log.info("查询到分类数量: {}", categories.size());
        return categories;
    }

    @Override
    public Category getById(Long id) {
        log.info("查询分类详情 - id: {}", id);
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            log.warn("分类不存在 - id: {}", id);
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    @Override
    public void create(CategoryDTO categoryDTO) {
        log.info("创建分类 - name: {}", categoryDTO.getName());
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setSortOrder(categoryDTO.getSortOrder());
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);
        log.info("分类创建成功 - id: {}", category.getId());
    }

    @Override
    public void update(Long id, CategoryDTO categoryDTO) {
        log.info("更新分类 - id: {}, name: {}", id, categoryDTO.getName());
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            log.warn("分类不存在 - id: {}", id);
            throw new BusinessException("分类不存在");
        }
        category.setName(categoryDTO.getName());
        category.setSortOrder(categoryDTO.getSortOrder());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);
        log.info("分类更新成功 - id: {}", id);
    }

    @Override
    public void delete(Long id) {
        log.info("删除分类 - id: {}", id);
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            log.warn("分类不存在 - id: {}", id);
            throw new BusinessException("分类不存在");
        }
        Integer count = dishMapper.countByCategory(id);
        if (count != null && count > 0) {
            log.warn("分类下还有菜品，无法删除 - id: {}, count: {}", id, count);
            throw new BusinessException("该分类下还有菜品，无法删除");
        }
        categoryMapper.deleteById(id);
        log.info("分类删除成功 - id: {}", id);
    }
}