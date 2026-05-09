package com.example.yitiaoyu.server.controller;

import com.example.yitiaoyu.common.Result;
import com.example.yitiaoyu.pojo.dto.ComboDTO;
import com.example.yitiaoyu.pojo.entity.Combo;
import com.example.yitiaoyu.pojo.vo.ComboVO;
import com.example.yitiaoyu.pojo.vo.PageVO;
import com.example.yitiaoyu.server.service.ComboService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/combo")
public class ComboController {

    private final ComboService comboService;

    public ComboController(ComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping
    public Result<PageVO<ComboVO>> list(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(comboService.list(page, size));
    }

    @GetMapping("/{id}")
    public Result<ComboVO> getById(@PathVariable Long id) {
        return Result.success(comboService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody ComboDTO comboDTO) {
        comboService.create(comboDTO);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ComboDTO comboDTO) {
        comboService.update(id, comboDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        comboService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        comboService.updateStatus(id, status);
        return Result.success();
    }
}

@RestController
@RequestMapping("/api/combo")
class ComboPublicController {

    private final ComboService comboService;

    public ComboPublicController(ComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping
    public Result<List<Combo>> list() {
        return Result.success(comboService.listEnabled());
    }

    @GetMapping("/{id}")
    public Result<ComboVO> getById(@PathVariable Long id) {
        return Result.success(comboService.getById(id));
    }
}