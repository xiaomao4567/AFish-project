package com.example.yitiaoyu.server.controller;

import com.example.yitiaoyu.common.Result;
import com.example.yitiaoyu.pojo.dto.CategoryDTO;
import com.example.yitiaoyu.pojo.entity.Category;
import com.example.yitiaoyu.server.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody CategoryDTO categoryDTO) {
        categoryService.create(categoryDTO);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        categoryService.update(id, categoryDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}

@RestController
@RequestMapping("/api/category")
class CategoryPublicController {

    private final CategoryService categoryService;

    public CategoryPublicController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }
}