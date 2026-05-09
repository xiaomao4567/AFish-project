package com.example.yitiaoyu.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComboItemDTO {
    private Long dishId;
    private String flavor;
    private Integer quantity;
}