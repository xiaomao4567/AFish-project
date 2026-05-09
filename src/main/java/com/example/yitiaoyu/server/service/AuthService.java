package com.example.yitiaoyu.server.service;

import com.example.yitiaoyu.pojo.dto.LoginDTO;
import com.example.yitiaoyu.pojo.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO loginDTO);
}