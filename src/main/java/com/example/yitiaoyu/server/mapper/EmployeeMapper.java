package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    Employee selectById(Long id);

    void insert(Employee employee);

    void updateById(Employee employee);

    void deleteById(Long id);

    Employee selectByUsername(String username);

    List<Employee> selectAll();
}
