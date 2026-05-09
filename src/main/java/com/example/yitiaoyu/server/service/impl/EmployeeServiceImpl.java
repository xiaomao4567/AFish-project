package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.pojo.dto.EmployeeDTO;
import com.example.yitiaoyu.pojo.entity.Employee;
import com.example.yitiaoyu.pojo.vo.EmployeeVO;
import com.example.yitiaoyu.pojo.vo.PageVO;
import com.example.yitiaoyu.server.mapper.EmployeeMapper;
import com.example.yitiaoyu.server.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public PageVO<EmployeeVO> list(Integer page, Integer size) {
        log.info("查询员工列表 - page: {}, size: {}", page, size);
        List<Employee> employees = employeeMapper.selectAll();
        int start = (page - 1) * size;
        int end = Math.min(start + size, employees.size());
        List<Employee> pageContent = start < employees.size() ? employees.subList(start, end) : List.of();
        log.info("查询到员工数量: {}", employees.size());
        return new PageVO<>(
                pageContent.stream().map(this::convertToVO).toList(),
                (long) employees.size(),
                page,
                size
        );
    }

    @Override
    public EmployeeVO getById(Long id) {
        log.info("查询员工详情 - id: {}", id);
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            log.warn("员工不存在 - id: {}", id);
            throw new BusinessException("员工不存在");
        }
        return convertToVO(employee);
    }

    @Override
    public void create(EmployeeDTO employeeDTO) {
        log.info("创建员工 - username: {}", employeeDTO.getUsername());
        if (employeeMapper.selectByUsername(employeeDTO.getUsername()) != null) {
            log.warn("用户名已存在 - username: {}", employeeDTO.getUsername());
            throw new BusinessException("用户名已存在");
        }
        Employee employee = new Employee();
        employee.setUsername(employeeDTO.getUsername());
        employee.setPassword(employeeDTO.getPassword());
        employee.setRealName(employeeDTO.getRealName());
        employee.setRole(employeeDTO.getRole());
        employee.setPhone(employeeDTO.getPhone());
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employeeMapper.insert(employee);
        log.info("员工创建成功 - id: {}", employee.getId());
    }

    @Override
    public void update(Long id, EmployeeDTO employeeDTO) {
        log.info("更新员工 - id: {}, realName: {}", id, employeeDTO.getRealName());
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            log.warn("员工不存在 - id: {}", id);
            throw new BusinessException("员工不存在");
        }
        employee.setRealName(employeeDTO.getRealName());
        employee.setRole(employeeDTO.getRole());
        employee.setPhone(employeeDTO.getPhone());
        employee.setUpdateTime(LocalDateTime.now());
        employeeMapper.updateById(employee);
        log.info("员工更新成功 - id: {}", id);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        log.info("删除员工 - id: {}, currentUserId: {}", id, currentUserId);
        if (id.equals(currentUserId)) {
            log.warn("不能删除当前登录账号 - id: {}", id);
            throw new BusinessException("不能删除当前登录账号");
        }
        if (employeeMapper.selectById(id) == null) {
            log.warn("员工不存在 - id: {}", id);
            throw new BusinessException("员工不存在");
        }
        employeeMapper.deleteById(id);
        log.info("员工删除成功 - id: {}", id);
    }

    private EmployeeVO convertToVO(Employee employee) {
        EmployeeVO vo = new EmployeeVO();
        vo.setId(employee.getId());
        vo.setUsername(employee.getUsername());
        vo.setRealName(employee.getRealName());
        vo.setRole(employee.getRole());
        vo.setPhone(employee.getPhone());
        vo.setCreateTime(employee.getCreateTime());
        return vo;
    }
}