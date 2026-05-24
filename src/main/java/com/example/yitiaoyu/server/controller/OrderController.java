package com.example.yitiaoyu.server.controller;

import com.example.yitiaoyu.util.JwtUtil;
import com.example.yitiaoyu.common.Result;
import com.example.yitiaoyu.pojo.dto.OrderDTO;
import com.example.yitiaoyu.pojo.vo.OrderVO;
import com.example.yitiaoyu.pojo.vo.PageVO;
import com.example.yitiaoyu.server.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<OrderVO> create(@RequestBody OrderDTO orderDTO) {
        return Result.success(orderService.create(orderDTO));
    }

    @GetMapping("/table/{tableNumber}")
    public Result<List<OrderVO>> listByTable(@PathVariable Integer tableNumber) {
        return Result.success(orderService.listByTable(tableNumber));
    }
}

@RestController
@RequestMapping("/api/admin/order")
class OrderAdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public Result<PageVO<OrderVO>> list(@RequestParam(required = false) String status,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.list(status, startTime, endTime, page, size));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String role = jwtUtil.getRole(token);
        orderService.updateStatus(id, status, role);
        return Result.success();
    }
}
