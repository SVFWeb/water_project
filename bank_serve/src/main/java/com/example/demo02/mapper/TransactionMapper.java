package com.example.demo02.mapper;

import com.example.demo02.domain.Transaction;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TransactionMapper {

    // 查询所有交易记录
    @Select("SELECT transaction_id, user_id, machine_id, order_status, total_liters, " +
            "final_amount, start_time, end_time FROM transaction")
    List<Transaction> findAll();

    // 根据交易ID查询
    @Select("SELECT transaction_id, user_id, machine_id, order_status, total_liters, " +
            "final_amount, start_time, end_time FROM transaction " +
            "WHERE transaction_id = #{transactionId}")
    Transaction findByTransactionId(@Param("transactionId") String transactionId);

    // 根据用户ID查询交易记录
    @Select("SELECT transaction_id, user_id, machine_id, order_status, total_liters, " +
            "final_amount, start_time, end_time FROM transaction " +
            "WHERE user_id = #{userId} ORDER BY start_time DESC")
    List<Transaction> findByUserId(@Param("userId") String userId);

    // 根据设备ID查询交易记录
    @Select("SELECT transaction_id, user_id, machine_id, order_status, total_liters, " +
            "final_amount, start_time, end_time FROM transaction " +
            "WHERE machine_id = #{machineId} ORDER BY start_time DESC")
    List<Transaction> findByMachineId(@Param("machineId") String machineId);

    // 根据订单状态查询交易记录
    @Select("SELECT transaction_id, user_id, machine_id, order_status, total_liters, " +
            "final_amount, start_time, end_time FROM transaction " +
            "WHERE order_status = #{orderStatus} ORDER BY start_time DESC")
    List<Transaction> findByOrderStatus(@Param("orderStatus") String orderStatus);

    // 插入交易记录
    @Insert("INSERT INTO transaction (transaction_id, user_id, machine_id, order_status, " +
            "total_liters, final_amount, start_time, end_time) " +
            "VALUES (#{transactionId}, #{userId}, #{machineId}, #{orderStatus}, " +
            "#{totalLiters}, #{finalAmount}, #{startTime}, #{endTime})")
    int insert(Transaction transaction);

    // 更新交易记录
    @Update("UPDATE transaction SET user_id = #{userId}, machine_id = #{machineId}, " +
            "order_status = #{orderStatus}, total_liters = #{totalLiters}, " +
            "final_amount = #{finalAmount}, start_time = #{startTime}, " +
            "end_time = #{endTime} WHERE transaction_id = #{transactionId}")
    int update(Transaction transaction);

    // 更新订单状态
    @Update("UPDATE transaction SET order_status = #{orderStatus} WHERE transaction_id = #{transactionId}")
    int updateOrderStatus(@Param("transactionId") String transactionId, @Param("orderStatus") String orderStatus);

    // 完成交易（更新结束信息）
    @Update("UPDATE transaction SET total_liters = #{totalLiters}, final_amount = #{finalAmount}, " +
            "end_time = #{endTime}, order_status = #{orderStatus} WHERE transaction_id = #{transactionId}")
    int completeTransaction(@Param("transactionId") String transactionId,
                            @Param("totalLiters") Double totalLiters,
                            @Param("finalAmount") Double finalAmount,
                            @Param("endTime") String endTime,
                            @Param("orderStatus") String orderStatus);

    // 开始交易（设置开始时间）
    @Update("UPDATE transaction SET start_time = #{startTime}, order_status = '进行中' " +
            "WHERE transaction_id = #{transactionId}")
    int startTransaction(@Param("transactionId") String transactionId, @Param("startTime") String startTime);

    // 删除交易记录
    @Delete("DELETE FROM transaction WHERE transaction_id = #{transactionId}")
    int deleteById(@Param("transactionId") String transactionId);

    // 检查交易是否存在
    @Select("SELECT COUNT(*) FROM transaction WHERE transaction_id = #{transactionId}")
    int existsByTransactionId(@Param("transactionId") String transactionId);

    // 分页查询
    @Select("SELECT transaction_id, user_id, machine_id, order_status, total_liters, " +
            "final_amount, start_time, end_time FROM transaction " +
            "ORDER BY start_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Transaction> findWithPagination(@Param("offset") int offset, @Param("size") int size);

    // 获取总记录数
    @Select("SELECT COUNT(*) FROM transaction")
    int getTotalCount();

    // 统计用户交易总额
    @Select("SELECT SUM(final_amount) FROM transaction WHERE user_id = #{userId}")
    Double getTotalAmountByUserId(@Param("userId") String userId);

    // 统计设备交易次数
    @Select("SELECT COUNT(*) FROM transaction WHERE machine_id = #{machineId}")
    int getTransactionCountByMachineId(@Param("machineId") String machineId);




}
