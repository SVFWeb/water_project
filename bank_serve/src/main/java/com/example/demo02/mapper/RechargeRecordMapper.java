package com.example.demo02.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo02.domain.RechargeRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {

    // 查询所有充值记录
    @Select("SELECT * FROM record")
    List<RechargeRecord> findAll();

    // 根据记录ID查询
    @Select("SELECT * FROM record WHERE record_id = #{recordId}")
    RechargeRecord findByRecordId(@Param("recordId") String recordId);

    // 根据用户ID查询充值记录
    @Select("SELECT * FROM record WHERE user_id = #{userId}")
    List<RechargeRecord> findByUserId(@Param("userId") String userId);

    // 插入充值记录
    @Insert("INSERT INTO record (record_id, user_id, user_name, amount, recharge_time, status, remark) " +
            "VALUES (#{recordId}, #{userId}, #{userName}, #{amount}, #{rechargeTime}, #{status}, #{remark})")
    int insert(RechargeRecord rechargeRecord);

    // 更新充值记录
    @Update("UPDATE record SET user_id = #{userId}, user_name = #{userName}, amount = #{amount}, " +
            "recharge_time = #{rechargeTime}, status = #{status}, remark = #{remark} WHERE record_id = #{recordId}")
    int update(RechargeRecord rechargeRecord);

    // 删除充值记录
    @Delete("DELETE FROM record WHERE record_id = #{recordId}")
    int deleteById(@Param("recordId") String recordId);

    // 检查记录是否存在
    @Select("SELECT COUNT(*) FROM record WHERE record_id = #{recordId}")
    int existsByRecordId(@Param("recordId") String recordId);

    // 根据用户ID统计充值记录数量
    @Select("SELECT COUNT(*) FROM record WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") String userId);

    // 统计用户总充值金额
    @Select("SELECT COALESCE(SUM(amount), 0) FROM record WHERE user_id = #{userId} AND status = 'success'")
    Double getTotalRechargeAmountByUserId(@Param("userId") String userId);

    // 根据状态查询充值记录
    @Select("SELECT * FROM record WHERE status = #{status}")
    List<RechargeRecord> findByStatus(@Param("status") String status);

    // 更新充值状态
    @Update("UPDATE record SET status = #{status} WHERE record_id = #{recordId}")
    int updateStatus(@Param("recordId") String recordId, @Param("status") String status);

    // 统计总记录数
    @Select("SELECT COUNT(*) FROM record")
    int countAll();
}