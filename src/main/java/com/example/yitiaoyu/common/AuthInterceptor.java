package com.example.yitiaoyu.common;

import com.example.yitiaoyu.pojo.entity.Employee;
import com.example.yitiaoyu.server.mapper.EmployeeMapper;
import com.example.yitiaoyu.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long userId = jwtUtil.getUserId(token);
            if (userId != null) {
                try {
                    Employee employee = employeeMapper.selectById(userId);
                    if (employee != null) {
                        UserContext.setUser(employee);
                        log.info("用户登录 - userId: {}, username: {}, role: {}", 
                                employee.getId(), employee.getUsername(), employee.getRole());
                        return true;
                    }
                } catch (Exception e) {
                    log.error("获取用户信息失败: {}", e.getMessage());
                }
            }
        }
        response.setStatus(401);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
