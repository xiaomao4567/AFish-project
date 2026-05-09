package com.example.yitiaoyu.server.service;

import com.example.yitiaoyu.pojo.dto.DishDTO;
import com.example.yitiaoyu.pojo.entity.Dish;
import com.example.yitiaoyu.pojo.vo.DishVO;
import com.example.yitiaoyu.pojo.vo.PageVO;

import java.util.List;

public interface DishService {
    PageVO<DishVO> list(Long categoryId, String name, Integer page, Integer size);
    DishVO getById(Long id);
    void create(DishDTO dishDTO);
    void update(Long id, DishDTO dishDTO);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
    List<Dish> listByCategory(Long categoryId);
}