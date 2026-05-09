package com.example.yitiaoyu.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComboDTO {
    private String name;
    private String image;
    private String description;
    private BigDecimal price;
    private Integer status;
    private List<ComboItemDTO> items;
}