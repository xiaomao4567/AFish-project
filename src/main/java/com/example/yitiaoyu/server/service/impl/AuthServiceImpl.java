package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.common.JwtUtil;
import com.example.yitiaoyu.pojo.dto.LoginDTO;
import com.example.yitiaoyu.pojo.entity.Employee;
import com.example.yitiaoyu.pojo.vo.LoginVO;
import com.example.yitiaoyu.server.mapper.EmployeeMapper;
import com.example.yitiaoyu.server.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final EmployeeMapper employeeMapper;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(EmployeeMapper employeeMapper, JwtUtil jwtUtil) {
        this.employeeMapper = employeeMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        Employee employee = employeeMapper.selectByUsername(loginDTO.getUsername());
        if (employee == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!loginDTO.getPassword().equals(employee.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(employee.getId(), employee.getUsername(), employee.getRole());
        LoginVO loginVO = new LoginVO();
        loginVO.setId(employee.getId());
        loginVO.setUsername(employee.getUsername());
        loginVO.setRealName(employee.getRealName());
        loginVO.setRole(employee.getRole());
        loginVO.setToken(token);
        return loginVO;
    }
}