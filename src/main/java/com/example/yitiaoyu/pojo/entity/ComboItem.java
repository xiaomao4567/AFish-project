package com.example.yitiaoyu.pojo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComboItem {

    private Long id;
    private Long comboId;
    private Long dishId;
    private String flavor;
    private Integer quantity;
    private LocalDateTime createTime;
}