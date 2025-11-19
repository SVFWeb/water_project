package com.example.demo02.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo02.domain.Transaction;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {

    // 查询所有交易记录
    @Select("SELECT * FROM transaction")
    List<Transaction> findAll();

    // 根据交易ID查询
    @Select("SELECT * FROM transaction WHERE transaction_id = #{transactionId}")
    Transaction findByTransactionId(@Param("transactionId") String transactionId);

    // 根据用户ID查询交易记录
    @Select("SELECT * FROM transaction WHERE user_id = #{userId}")
    List<Transaction> findByUserId(@Param("userId") String userId);

    // 根据设备ID查询交易记录
    @Select("SELECT * FROM transaction WHERE machine_id = #{machineId}")
    List<Transaction> findByMachineId(@Param("machineId") String machineId);

    // 根据订单状态查询交易记录
    @Select("SELECT * FROM transaction WHERE order_status = #{orderStatus}")
    List<Transaction> findByOrderStatus(@Param("orderStatus") String orderStatus);

    // 插入交易记录
    @Insert("INSERT INTO transaction (transaction_id, user_id, user_name, machine_id, order_status, " +
            "total_liters, final_amount, start_time, end_time) " +
            "VALUES (#{transactionId}, #{userId}, #{userName}, #{machineId}, #{orderStatus}, " +
            "#{totalLiters}, #{finalAmount}, #{startTime}, #{endTime})")
    int insert(Transaction transaction);

    // 更新交易记录
    @Update("UPDATE transaction SET user_id = #{userId}, user_name = #{userName}, machine_id = #{machineId}, " +
            "order_status = #{orderStatus}, total_liters = #{totalLiters}, final_amount = #{finalAmount}, " +
            "start_time = #{startTime}, end_time = #{endTime} WHERE transaction_id = #{transactionId}")
    int update(Transaction transaction);

    // 删除交易记录
    @Delete("DELETE FROM transaction WHERE transaction_id = #{transactionId}")
    int deleteById(@Param("transactionId") String transactionId);

    // 检查交易记录是否存在
    @Select("SELECT COUNT(*) FROM transaction WHERE transaction_id = #{transactionId}")
    int existsByTransactionId(@Param("transactionId") String transactionId);

    // 更新订单状态
    @Update("UPDATE transaction SET order_status = #{orderStatus} WHERE transaction_id = #{transactionId}")
    int updateOrderStatus(@Param("transactionId") String transactionId, @Param("orderStatus") String orderStatus);

    // 更新交易完成信息
    @Update("UPDATE transaction SET total_liters = #{totalLiters}, final_amount = #{finalAmount}, " +
            "end_time = #{endTime}, order_status = #{orderStatus} WHERE transaction_id = #{transactionId}")
    int completeTransaction(@Param("transactionId") String transactionId,
                            @Param("totalLiters") Double totalLiters,
                            @Param("finalAmount") Double finalAmount,
                            @Param("endTime") java.time.LocalDateTime endTime,
                            @Param("orderStatus") String orderStatus);

    // 统计交易记录数量
    @Select("SELECT COUNT(*) FROM transaction")
    int countAll();

    // 根据用户ID统计交易数量
    @Select("SELECT COUNT(*) FROM transaction WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") String userId);

    // 查询用户交易总额
    @Select("SELECT COALESCE(SUM(final_amount), 0) FROM transaction WHERE user_id = #{userId}")
    Double getTotalAmountByUserId(@Param("userId") String userId);
}