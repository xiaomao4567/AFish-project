package com.example.yitiaoyu.server.controller;

import com.example.yitiaoyu.common.Result;
import com.example.yitiaoyu.pojo.dto.CartItemDTO;
import com.example.yitiaoyu.pojo.vo.CartVO;
import com.example.yitiaoyu.server.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{tableNumber}")
    public Result<CartVO> getByTable(@PathVariable Integer tableNumber) {
        return Result.success(cartService.getByTable(tableNumber));
    }

    @PostMapping("/{tableNumber}/items")
    public Result<Void> addItem(@PathVariable Integer tableNumber, @RequestBody CartItemDTO cartItemDTO) {
        cartService.addItem(tableNumber, cartItemDTO);
        return Result.success();
    }

    @PutMapping("/items/{itemId}")
    public Result<Void> updateItem(@PathVariable Long itemId, @RequestParam Integer quantity) {
        cartService.updateItem(itemId, quantity);
        return Result.success();
    }

    @DeleteMapping("/items/{itemId}")
    public Result<Void> removeItem(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        return Result.success();
    }

    @DeleteMapping("/{tableNumber}")
    public Result<Void> clear(@PathVariable Integer tableNumber) {
        cartService.clear(tableNumber);
        return Result.success();
    }
}