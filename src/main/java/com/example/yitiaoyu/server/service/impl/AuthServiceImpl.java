package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.common.JwtUtil;
import com.example.yitiaoyu.pojo.dto.LoginDTO;
import com.example.yitiaoyu.pojo.entity.Employee;
import com.example.yitiaoyu.pojo.vo.LoginVO;
import com.example.yitiaoyu.server.mapper.EmployeeMapper;
import com.example.yitiaoyu.server.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmployeeMapper employeeMapper;

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
}
