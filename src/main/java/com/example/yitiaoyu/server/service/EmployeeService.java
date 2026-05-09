package com.example.yitiaoyu.server.service;

import com.example.yitiaoyu.pojo.dto.EmployeeDTO;
import com.example.yitiaoyu.pojo.vo.EmployeeVO;
import com.example.yitiaoyu.pojo.vo.PageVO;

public interface EmployeeService {
    PageVO<EmployeeVO> list(Integer page, Integer size);
    EmployeeVO getById(Long id);
    void create(EmployeeDTO employeeDTO);
    void update(Long id, EmployeeDTO employeeDTO);
    void delete(Long id, Long currentUserId);
}