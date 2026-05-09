package com.example.yitiaoyu.server.service;

import com.example.yitiaoyu.pojo.vo.StatisticsVO;

import java.time.LocalDateTime;

public interface StatisticsService {
    StatisticsVO getStatistics(LocalDateTime startTime, LocalDateTime endTime);
}