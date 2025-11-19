package com.example.demo02.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo02.domain.RateConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RateConfigMapper extends BaseMapper<RateConfig> {

    // 查询所有费率配置
    @Select("SELECT * FROM rate_config")
    List<RateConfig> findAll();

    // 根据费率ID查询
    @Select("SELECT * FROM rate_config WHERE rate_id = #{rateId}")
    RateConfig findByRateId(@Param("rateId") String rateId);

    // 根据设备ID查询费率配置
    @Select("SELECT * FROM rate_config WHERE machine_id = #{machineId}")
    List<RateConfig> findByMachineId(@Param("machineId") String machineId);

    // 插入费率配置
    @Insert("INSERT INTO rate_config (rate_id, rate_day_rate, machine_id, service_fee, price_per_liter) " +
            "VALUES (#{rateId}, #{rateDayRate}, #{machineId}, #{serviceFee}, #{pricePerLiter})")
    int insert(RateConfig rateConfig);

    // 更新费率配置
    @Update("UPDATE rate_config SET rate_day_rate = #{rateDayRate}, machine_id = #{machineId}, " +
            "service_fee = #{serviceFee}, price_per_liter = #{pricePerLiter} WHERE rate_id = #{rateId}")
    int update(RateConfig rateConfig);

    // 删除费率配置
    @Delete("DELETE FROM rate_config WHERE rate_id = #{rateId}")
    int deleteById(@Param("rateId") String rateId);

    // 检查费率配置是否存在
    @Select("SELECT COUNT(*) FROM rate_config WHERE rate_id = #{rateId}")
    int existsByRateId(@Param("rateId") String rateId);

    // 检查设备是否已有费率配置
    @Select("SELECT COUNT(*) FROM rate_config WHERE machine_id = #{machineId}")
    int existsByMachineId(@Param("machineId") String machineId);

    // 根据设备ID删除费率配置
    @Delete("DELETE FROM rate_config WHERE machine_id = #{machineId}")
    int deleteByMachineId(@Param("machineId") String machineId);

    // 更新服务费
    @Update("UPDATE rate_config SET service_fee = #{serviceFee} WHERE rate_id = #{rateId}")
    int updateServiceFee(@Param("rateId") String rateId, @Param("serviceFee") Double serviceFee);

    // 更新每升价格
    @Update("UPDATE rate_config SET price_per_liter = #{pricePerLiter} WHERE rate_id = #{rateId}")
    int updatePricePerLiter(@Param("rateId") String rateId, @Param("pricePerLiter") Double pricePerLiter);

    // 更新日费率
    @Update("UPDATE rate_config SET rate_day_rate = #{rateDayRate} WHERE rate_id = #{rateId}")
    int updateRateDayRate(@Param("rateId") String rateId, @Param("rateDayRate") String rateDayRate);

    // 统计费率配置数量
    @Select("SELECT COUNT(*) FROM rate_config")
    int countAll();
}