package com.example.yitiaoyu.server.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

public interface ExportService {
    ByteArrayOutputStream exportOrders(LocalDateTime startTime, LocalDateTime endTime);
}