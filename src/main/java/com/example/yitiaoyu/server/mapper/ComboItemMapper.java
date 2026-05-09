package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.ComboItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ComboItemMapper {

    ComboItem selectById(Long id);

    void insert(ComboItem comboItem);

    void updateById(ComboItem comboItem);

    void deleteById(Long id);

    List<ComboItem> selectByComboId(Long comboId);

    void deleteByComboId(Long comboId);
}
