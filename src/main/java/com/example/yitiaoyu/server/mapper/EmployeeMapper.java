package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Select("SELECT * FROM employee WHERE id = #{id}")
    Employee selectById(Long id);

    @Insert("INSERT INTO employee (username, password, real_name, role, phone, create_time, update_time) VALUES (#{username}, #{password}, #{realName}, #{role}, #{phone}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Employee employee);

    @Update("UPDATE employee SET username = #{username}, password = #{password}, real_name = #{realName}, role = #{role}, phone = #{phone}, update_time = #{updateTime} WHERE id = #{id}")
    void updateById(Employee employee);

    @Delete("DELETE FROM employee WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM employee WHERE username = #{username}")
    Employee selectByUsername(String username);

    @Select("SELECT * FROM employee ORDER BY create_time DESC")
    List<Employee> selectAll();
}