package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.pojo.dto.DishDTO;
import com.example.yitiaoyu.pojo.entity.Category;
import com.example.yitiaoyu.pojo.entity.Dish;
import com.example.yitiaoyu.pojo.vo.DishVO;
import com.example.yitiaoyu.pojo.vo.PageVO;
import com.example.yitiaoyu.server.mapper.CategoryMapper;
import com.example.yitiaoyu.server.mapper.DishMapper;
import com.example.yitiaoyu.server.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;
    private final CategoryMapper categoryMapper;

    public DishServiceImpl(DishMapper dishMapper, CategoryMapper categoryMapper) {
        this.dishMapper = dishMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public PageVO<DishVO> list(Long categoryId, String name, Integer page, Integer size) {
        log.info("查询菜品列表 - categoryId: {}, name: {}, page: {}, size: {}", categoryId, name, page, size);
        List<Dish> dishes;
        if (categoryId != null && name != null && !name.isEmpty()) {
            dishes = dishMapper.selectByCategoryAndName(categoryId, name);
        } else if (categoryId != null) {
            dishes = dishMapper.selectByCategoryAll(categoryId);
        } else if (name != null && !name.isEmpty()) {
            dishes = dishMapper.selectByName(name);
        } else {
            dishes = dishMapper.selectAll();
        }
        int start = (page - 1) * size;
        int end = Math.min(start + size, dishes.size());
        List<Dish> pageContent = start < dishes.size() ? dishes.subList(start, end) : List.of();
        log.info("查询到菜品数量: {}", dishes.size());
        return new PageVO<>(
                pageContent.stream().map(this::convertToVO).toList(),
                (long) dishes.size(),
                page,
                size
        );
    }

    @Override
    public DishVO getById(Long id) {
        log.info("查询菜品详情 - id: {}", id);
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            log.warn("菜品不存在 - id: {}", id);
            throw new BusinessException("菜品不存在");
        }
        return convertToVO(dish);
    }

    @Override
    public void create(DishDTO dishDTO) {
        log.info("创建菜品 - name: {}", dishDTO.getName());
        Dish dish = new Dish();
        dish.setName(dishDTO.getName());
        dish.setCategoryId(dishDTO.getCategoryId());
        dish.setImage(dishDTO.getImage());
        dish.setDescription(dishDTO.getDescription());
        dish.setFlavors(dishDTO.getFlavors());
        dish.setPrice(dishDTO.getPrice());
        dish.setStatus(dishDTO.getStatus() != null ? dishDTO.getStatus() : 1);
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        dishMapper.insert(dish);
        log.info("菜品创建成功 - id: {}", dish.getId());
    }

    @Override
    public void update(Long id, DishDTO dishDTO) {
        log.info("更新菜品 - id: {}, name: {}", id, dishDTO.getName());
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            log.warn("菜品不存在 - id: {}", id);
            throw new BusinessException("菜品不存在");
        }
        dish.setName(dishDTO.getName());
        dish.setCategoryId(dishDTO.getCategoryId());
        dish.setImage(dishDTO.getImage());
        dish.setDescription(dishDTO.getDescription());
        dish.setFlavors(dishDTO.getFlavors());
        dish.setPrice(dishDTO.getPrice());
        if (dishDTO.getStatus() != null) {
            dish.setStatus(dishDTO.getStatus());
        }
        dish.setUpdateTime(LocalDateTime.now());
        dishMapper.updateById(dish);
        log.info("菜品更新成功 - id: {}", id);
    }

    @Override
    public void delete(Long id) {
        log.info("删除菜品 - id: {}", id);
        if (dishMapper.selectById(id) == null) {
            log.warn("菜品不存在 - id: {}", id);
            throw new BusinessException("菜品不存在");
        }
        dishMapper.deleteById(id);
        log.info("菜品删除成功 - id: {}", id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        log.info("更新菜品状态 - id: {}, status: {}", id, status);
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            log.warn("菜品不存在 - id: {}", id);
            throw new BusinessException("菜品不存在");
        }
        dish.setStatus(status);
        dish.setUpdateTime(LocalDateTime.now());
        dishMapper.updateById(dish);
        log.info("菜品状态更新成功 - id: {}, status: {}", id, status);
    }

    @Override
    public List<Dish> listByCategory(Long categoryId) {
        log.info("查询分类下的菜品 - categoryId: {}", categoryId);
        return dishMapper.selectByCategory(categoryId);
    }

    private DishVO convertToVO(Dish dish) {
        DishVO vo = new DishVO();
        vo.setId(dish.getId());
        vo.setName(dish.getName());
        vo.setCategoryId(dish.getCategoryId());
        if (dish.getCategoryId() != null) {
            Category category = categoryMapper.selectById(dish.getCategoryId());
            vo.setCategoryName(category != null ? category.getName() : "");
        }
        vo.setImage(dish.getImage());
        vo.setDescription(dish.getDescription());
        vo.setFlavors(dish.getFlavors() != null ? dish.getFlavors().split(",") : new String[0]);
        vo.setPrice(dish.getPrice());
        vo.setStatus(dish.getStatus());
        vo.setCreateTime(dish.getCreateTime());
        return vo;
    }
}