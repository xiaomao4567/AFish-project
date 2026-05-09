package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.common.BusinessException;
import com.example.yitiaoyu.pojo.dto.ComboDTO;
import com.example.yitiaoyu.pojo.dto.ComboItemDTO;
import com.example.yitiaoyu.pojo.entity.Combo;
import com.example.yitiaoyu.pojo.entity.ComboItem;
import com.example.yitiaoyu.pojo.entity.Dish;
import com.example.yitiaoyu.pojo.vo.ComboItemVO;
import com.example.yitiaoyu.pojo.vo.ComboVO;
import com.example.yitiaoyu.pojo.vo.PageVO;
import com.example.yitiaoyu.server.mapper.ComboItemMapper;
import com.example.yitiaoyu.server.mapper.ComboMapper;
import com.example.yitiaoyu.server.mapper.DishMapper;
import com.example.yitiaoyu.server.service.ComboService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ComboServiceImpl implements ComboService {

    private final ComboMapper comboMapper;
    private final ComboItemMapper comboItemMapper;
    private final DishMapper dishMapper;

    public ComboServiceImpl(ComboMapper comboMapper, ComboItemMapper comboItemMapper, DishMapper dishMapper) {
        this.comboMapper = comboMapper;
        this.comboItemMapper = comboItemMapper;
        this.dishMapper = dishMapper;
    }

    @Override
    public PageVO<ComboVO> list(Integer page, Integer size) {
        log.info("查询套餐列表 - page: {}, size: {}", page, size);
        List<Combo> combos = comboMapper.selectAll();
        int start = (page - 1) * size;
        int end = Math.min(start + size, combos.size());
        List<Combo> pageContent = start < combos.size() ? combos.subList(start, end) : List.of();
        log.info("查询到套餐数量: {}", combos.size());
        return new PageVO<>(
                pageContent.stream().map(this::convertToVO).toList(),
                (long) combos.size(),
                page,
                size
        );
    }

    @Override
    public ComboVO getById(Long id) {
        log.info("查询套餐详情 - id: {}", id);
        Combo combo = comboMapper.selectById(id);
        if (combo == null) {
            log.warn("套餐不存在 - id: {}", id);
            throw new BusinessException("套餐不存在");
        }
        return convertToVO(combo);
    }

    @Override
    @Transactional
    public void create(ComboDTO comboDTO) {
        log.info("创建套餐 - name: {}", comboDTO.getName());
        if (comboMapper.selectByName(comboDTO.getName()) != null) {
            log.warn("套餐名称已存在 - name: {}", comboDTO.getName());
            throw new BusinessException("套餐名称已存在");
        }
        List<ComboItemDTO> items = comboDTO.getItems();
        if (items != null && !items.isEmpty()) {
            for (ComboItemDTO item : items) {
                Dish dish = dishMapper.selectById(item.getDishId());
                if (dish == null || dish.getStatus() != 1) {
                    log.warn("套餐包含未上架的菜品 - dishId: {}", item.getDishId());
                    throw new BusinessException("套餐包含未上架的菜品");
                }
            }
        }
        if (comboDTO.getPrice() == null) {
            log.warn("套餐价格不能为空");
            throw new BusinessException("套餐价格不能为空");
        }
        Combo combo = new Combo();
        combo.setName(comboDTO.getName());
        combo.setImage(comboDTO.getImage());
        combo.setDescription(comboDTO.getDescription());
        combo.setPrice(comboDTO.getPrice());
        combo.setStatus(comboDTO.getStatus() != null ? comboDTO.getStatus() : 1);
        combo.setCreateTime(LocalDateTime.now());
        combo.setUpdateTime(LocalDateTime.now());
        comboMapper.insert(combo);
        if (items != null && !items.isEmpty()) {
            for (ComboItemDTO item : items) {
                ComboItem comboItem = new ComboItem();
                comboItem.setComboId(combo.getId());
                comboItem.setDishId(item.getDishId());
                comboItem.setFlavor(item.getFlavor());
                comboItem.setQuantity(item.getQuantity());
                comboItem.setCreateTime(LocalDateTime.now());
                comboItemMapper.insert(comboItem);
            }
        }
        log.info("套餐创建成功 - id: {}", combo.getId());
    }

    @Override
    @Transactional
    public void update(Long id, ComboDTO comboDTO) {
        log.info("更新套餐 - id: {}, name: {}", id, comboDTO.getName());
        Combo combo = comboMapper.selectById(id);
        if (combo == null) {
            log.warn("套餐不存在 - id: {}", id);
            throw new BusinessException("套餐不存在");
        }
        Combo existing = comboMapper.selectByNameNotId(comboDTO.getName(), id);
        if (existing != null) {
            log.warn("套餐名称已存在 - name: {}", comboDTO.getName());
            throw new BusinessException("套餐名称已存在");
        }
        List<ComboItemDTO> items = comboDTO.getItems();
        if (items != null && !items.isEmpty()) {
            for (ComboItemDTO item : items) {
                Dish dish = dishMapper.selectById(item.getDishId());
                if (dish == null || dish.getStatus() != 1) {
                    log.warn("套餐包含未上架的菜品 - dishId: {}", item.getDishId());
                    throw new BusinessException("套餐包含未上架的菜品");
                }
            }
        }
        combo.setName(comboDTO.getName());
        combo.setImage(comboDTO.getImage());
        combo.setDescription(comboDTO.getDescription());
        combo.setPrice(comboDTO.getPrice());
        if (comboDTO.getStatus() != null) {
            combo.setStatus(comboDTO.getStatus());
        }
        combo.setUpdateTime(LocalDateTime.now());
        comboMapper.updateById(combo);
        comboItemMapper.deleteByComboId(id);
        if (items != null && !items.isEmpty()) {
            for (ComboItemDTO item : items) {
                ComboItem comboItem = new ComboItem();
                comboItem.setComboId(id);
                comboItem.setDishId(item.getDishId());
                comboItem.setFlavor(item.getFlavor());
                comboItem.setQuantity(item.getQuantity());
                comboItem.setCreateTime(LocalDateTime.now());
                comboItemMapper.insert(comboItem);
            }
        }
        log.info("套餐更新成功 - id: {}", id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除套餐 - id: {}", id);
        if (comboMapper.selectById(id) == null) {
            log.warn("套餐不存在 - id: {}", id);
            throw new BusinessException("套餐不存在");
        }
        comboItemMapper.deleteByComboId(id);
        comboMapper.deleteById(id);
        log.info("套餐删除成功 - id: {}", id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        log.info("更新套餐状态 - id: {}, status: {}", id, status);
        Combo combo = comboMapper.selectById(id);
        if (combo == null) {
            log.warn("套餐不存在 - id: {}", id);
            throw new BusinessException("套餐不存在");
        }
        combo.setStatus(status);
        combo.setUpdateTime(LocalDateTime.now());
        comboMapper.updateById(combo);
        log.info("套餐状态更新成功 - id: {}, status: {}", id, status);
    }

    @Override
    public List<Combo> listEnabled() {
        log.info("查询可用套餐列表");
        return comboMapper.selectEnabled();
    }

    private ComboVO convertToVO(Combo combo) {
        ComboVO vo = new ComboVO();
        vo.setId(combo.getId());
        vo.setName(combo.getName());
        vo.setImage(combo.getImage());
        vo.setDescription(combo.getDescription());
        vo.setPrice(combo.getPrice());
        vo.setStatus(combo.getStatus());
        vo.setCreateTime(combo.getCreateTime());
        List<ComboItem> items = comboItemMapper.selectByComboId(combo.getId());
        vo.setItems(items.stream().map(this::convertItemToVO).toList());
        return vo;
    }

    private ComboItemVO convertItemToVO(ComboItem item) {
        ComboItemVO vo = new ComboItemVO();
        vo.setDishId(item.getDishId());
        Dish dish = dishMapper.selectById(item.getDishId());
        if (dish != null) {
            vo.setDishName(dish.getName());
            vo.setImage(dish.getImage());
            vo.setPrice(dish.getPrice());
        }
        vo.setFlavor(item.getFlavor());
        vo.setQuantity(item.getQuantity());
        return vo;
    }
}