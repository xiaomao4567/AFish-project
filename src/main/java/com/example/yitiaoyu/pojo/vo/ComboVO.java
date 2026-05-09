package com.example.yitiaoyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComboVO {
    private Long id;
    private String name;
    private String image;
    private String description;
    private BigDecimal price;
    private Integer status;
    private List<ComboItemVO> items;
    private LocalDateTime createTime;
}