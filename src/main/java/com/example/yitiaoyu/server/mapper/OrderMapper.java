package com.example.yitiaoyu.server.mapper;

import com.example.yitiaoyu.pojo.entity.Order;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order selectById(Long id);

    @Insert("INSERT INTO orders (order_no, table_number, total_amount, status, remark, create_time, pay_time, prepare_time, finish_time, update_time) VALUES (#{orderNo}, #{tableNumber}, #{totalAmount}, #{status}, #{remark}, #{createTime}, #{payTime}, #{prepareTime}, #{finishTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Order order);

    @Update("UPDATE orders SET order_no = #{orderNo}, table_number = #{tableNumber}, total_amount = #{totalAmount}, status = #{status}, remark = #{remark}, prepare_time = #{prepareTime}, finish_time = #{finishTime}, update_time = #{updateTime} WHERE id = #{id}")
    void updateById(Order order);

    @Delete("DELETE FROM orders WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM orders WHERE status = #{status} AND create_time >= #{startTime} AND create_time <= #{endTime} ORDER BY create_time DESC")
    List<Order> selectByStatusAndTimeRange(@Param("status") String status, 
                                           @Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM orders WHERE table_number = #{tableNumber} ORDER BY create_time DESC")
    List<Order> selectByTableNumber(@Param("tableNumber") Integer tableNumber);

    @Select("SELECT SUM(total_amount) FROM orders WHERE status = #{status} AND create_time >= #{startTime} AND create_time <= #{endTime}")
    BigDecimal sumTotalAmountByStatusAndTimeRange(@Param("status") String status, 
                                                  @Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status} AND create_time >= #{startTime} AND create_time <= #{endTime}")
    Integer countByStatusAndTimeRange(@Param("status") String status, 
                                      @Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM orders WHERE create_time >= #{startTime} AND create_time <= #{endTime} ORDER BY create_time DESC")
    List<Order> selectAllByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM orders WHERE status = #{status} ORDER BY create_time DESC")
    List<Order> selectByStatus(String status);

    @Select("SELECT * FROM orders ORDER BY create_time DESC")
    List<Order> selectAll();

    @Select("SELECT * FROM orders WHERE create_time >= #{startTime} ORDER BY create_time DESC")
    List<Order> selectByStartTime(@Param("startTime") LocalDateTime startTime);

    @Select("SELECT * FROM orders WHERE create_time <= #{endTime} ORDER BY create_time DESC")
    List<Order> selectByEndTime(@Param("endTime") LocalDateTime endTime);
}