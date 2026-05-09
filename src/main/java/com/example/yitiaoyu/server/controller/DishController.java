package com.example.yitiaoyu.server.controller;

import com.example.yitiaoyu.common.Result;
import com.example.yitiaoyu.pojo.dto.DishDTO;
import com.example.yitiaoyu.pojo.entity.Dish;
import com.example.yitiaoyu.pojo.vo.DishVO;
import com.example.yitiaoyu.pojo.vo.PageVO;
import com.example.yitiaoyu.server.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping
    public Result<PageVO<DishVO>> list(@RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishService.list(categoryId, name, page, size));
    }

    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody DishDTO dishDTO) {
        dishService.create(dishDTO);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishDTO dishDTO) {
        dishService.update(id, dishDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dishService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        dishService.updateStatus(id, status);
        return Result.success();
    }
}

@RestController
@RequestMapping("/api/dish")
class DishPublicController {

    @Autowired
    private DishService dishService;

    @GetMapping("/category/{categoryId}")
    public Result<List<Dish>> listByCategory(@PathVariable Long categoryId) {
        return Result.success(dishService.listByCategory(categoryId));
    }

    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }
}
