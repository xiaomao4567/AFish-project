package com.example.yitiaoyu.server.service.impl;

import com.example.yitiaoyu.pojo.entity.Order;
import com.example.yitiaoyu.pojo.entity.OrderItem;
import com.example.yitiaoyu.server.mapper.OrderItemMapper;
import com.example.yitiaoyu.server.mapper.OrderMapper;
import com.example.yitiaoyu.server.service.ExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public ExportServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public ByteArrayOutputStream exportOrders(LocalDateTime startTime, LocalDateTime endTime) {
        List<Order> orders = orderMapper.selectAllByTimeRange(startTime, endTime);
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("订单报表");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"订单号", "桌号", "菜品明细", "总金额", "下单时间", "订单状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            int rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Order order : orders) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getOrderNo());
                row.createCell(1).setCellValue(order.getTableNumber());
                List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
                StringBuilder itemsStr = new StringBuilder();
                for (int i = 0; i < items.size(); i++) {
                    OrderItem item = items.get(i);
                    if (i > 0) itemsStr.append("；");
                    itemsStr.append(item.getDishName())
                            .append("(").append(item.getFlavor()).append(")")
                            .append("x").append(item.getQuantity());
                }
                row.createCell(2).setCellValue(itemsStr.toString());
                row.createCell(3).setCellValue(order.getTotalAmount().doubleValue());
                row.createCell(4).setCellValue(order.getCreateTime().format(formatter));
                row.createCell(5).setCellValue(order.getStatus());
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
            return outputStream;
        } catch (IOException e) {
            throw new RuntimeException("导出失败", e);
        }
    }
}