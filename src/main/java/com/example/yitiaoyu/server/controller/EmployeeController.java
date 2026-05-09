package com.example.yitiaoyu.server.controller;

import com.example.yitiaoyu.common.JwtUtil;
import com.example.yitiaoyu.common.Result;
import com.example.yitiaoyu.pojo.dto.EmployeeDTO;
import com.example.yitiaoyu.pojo.vo.EmployeeVO;
import com.example.yitiaoyu.pojo.vo.PageVO;
import com.example.yitiaoyu.server.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public Result<PageVO<EmployeeVO>> list(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(employeeService.list(page, size));
    }

    @GetMapping("/{id}")
    public Result<EmployeeVO> getById(@PathVariable Long id) {
        return Result.success(employeeService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.create(employeeDTO);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        employeeService.update(id, employeeDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long currentUserId = jwtUtil.getUserId(token);
        employeeService.delete(id, currentUserId);
        return Result.success();
    }
}
