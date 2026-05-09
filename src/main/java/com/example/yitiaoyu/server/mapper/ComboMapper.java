package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.Combo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ComboMapper {

    Combo selectById(Long id);

    void insert(Combo combo);

    void updateById(Combo combo);

    void deleteById(Long id);

    List<Combo> selectAll();

    Combo selectByName(String name);

    Combo selectByNameNotId(@Param("name") String name, @Param("id") Long id);

    List<Combo> selectEnabled();
}