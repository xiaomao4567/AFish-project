package com.example.yitiaoyu.server.controller;

import com.example.yitiaoyu.common.Result;
import com.example.yitiaoyu.pojo.dto.LoginDTO;
import com.example.yitiaoyu.pojo.vo.LoginVO;
import com.example.yitiaoyu.server.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success(loginVO);
    }
}