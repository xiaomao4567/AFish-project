package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.common.JwtUtil;
import com.example.yitiaoyu.pojo.dto.LoginDTO;
import com.example.yitiaoyu.pojo.dto.WechatLoginDTO;
import com.example.yitiaoyu.pojo.entity.Employee;
import com.example.yitiaoyu.pojo.entity.User;
import com.example.yitiaoyu.pojo.vo.LoginVO;
import com.example.yitiaoyu.server.mapper.EmployeeMapper;
import com.example.yitiaoyu.server.mapper.UserMapper;
import com.example.yitiaoyu.server.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        log.info("【用户登录】用户名: {}", loginDTO.getUsername());
        Employee employee = employeeMapper.selectByUsername(loginDTO.getUsername());
        if (employee == null) {
            log.warn("【用户登录】用户名不存在 - username: {}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        if (!loginDTO.getPassword().equals(employee.getPassword())) {
            log.warn("【用户登录】密码错误 - username: {}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(employee.getId(), employee.getUsername(), employee.getRole());
        log.info("【用户登录】登录成功 - userId: {}, username: {}, role: {}", 
                employee.getId(), employee.getUsername(), employee.getRole());
        LoginVO loginVO = new LoginVO();
        loginVO.setId(employee.getId());
        loginVO.setUsername(employee.getUsername());
        loginVO.setRealName(employee.getRealName());
        loginVO.setRole(employee.getRole());
        loginVO.setToken(token);
        return loginVO;
    }

    @Override
    public LoginVO wechatLogin(WechatLoginDTO wechatLoginDTO) {
        log.info("【微信登录】code: {}", wechatLoginDTO.getCode());
        
        String openId = wechatLoginDTO.getCode();
        
        User user = userMapper.selectByOpenId(openId);
        
        if (user == null) {
            log.info("【微信登录】新用户注册 - openId: {}", openId);
            user = new User();
            user.setOpenId(openId);
            user.setNickname(wechatLoginDTO.getNickName() != null ? wechatLoginDTO.getNickName() : "微信用户");
            user.setAvatarUrl(wechatLoginDTO.getAvatarUrl());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
            log.info("【微信登录】用户注册成功 - userId: {}, nickname: {}", user.getId(), user.getNickname());
        } else {
            log.info("【微信登录】用户已存在 - userId: {}, nickname: {}", user.getId(), user.getNickname());
            user.setNickname(wechatLoginDTO.getNickName() != null ? wechatLoginDTO.getNickName() : user.getNickname());
            user.setAvatarUrl(wechatLoginDTO.getAvatarUrl() != null ? wechatLoginDTO.getAvatarUrl() : user.getAvatarUrl());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateByOpenId(user);
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getNickname(), "CUSTOMER");
        log.info("【微信登录】登录成功 - userId: {}, username: {}, role: CUSTOMER", user.getId(), user.getNickname());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setId(user.getId());
        loginVO.setUsername(user.getNickname());
        loginVO.setRealName(user.getNickname());
        loginVO.setRole("CUSTOMER");
        loginVO.setToken(token);
        
        return loginVO;
    }
}