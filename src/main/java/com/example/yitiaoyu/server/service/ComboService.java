package com.example.yitiaoyu.server.service;

import com.example.yitiaoyu.pojo.dto.ComboDTO;
import com.example.yitiaoyu.pojo.entity.Combo;
import com.example.yitiaoyu.pojo.vo.ComboVO;
import com.example.yitiaoyu.pojo.vo.PageVO;

import java.util.List;

public interface ComboService {
    PageVO<ComboVO> list(Integer page, Integer size);
    ComboVO getById(Long id);
    void create(ComboDTO comboDTO);
    void update(Long id, ComboDTO comboDTO);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
    List<Combo> listEnabled();
}