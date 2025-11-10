package com.example.demo02.mapper;


import com.example.demo02.domain.RateConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper  // 确保有这个注解
public interface RateConfigMapper {




    // 查询所有费率配置
    @Select("SELECT rate_id, machine_id, is_active, price_per_liter FROM rate_config")
    List<RateConfig> findAll();

    // 根据费率ID查询
    @Select("SELECT rate_id, machine_id, is_active, price_per_liter FROM rate_config WHERE rate_id = #{rateId}")
    RateConfig findByRateId(String rateId);

    // 根据设备ID查询费率配置
    @Select("SELECT rate_id, machine_id, is_active, price_per_liter FROM rate_config WHERE machine_id = #{machineId}")
    List<RateConfig> findByMachineId(String machineId);

    // 查询激活的费率配置
    @Select("SELECT rate_id, machine_id, is_active, price_per_liter FROM rate_config WHERE is_active = '1'")
    List<RateConfig> findActiveRates();

    // 根据设备ID查询激活的费率
    @Select("SELECT rate_id, machine_id, is_active, price_per_liter FROM rate_config WHERE machine_id = #{machineId} AND is_active = '1'")
    RateConfig findActiveRateByMachineId(String machineId);

    // 检查费率ID是否存在
    @Select("SELECT COUNT(*) FROM rate_config WHERE rate_id = #{rateId}")
    int existsByRateId(String rateId);

    // 检查设备是否已有费率配置
    @Select("SELECT COUNT(*) FROM rate_config WHERE machine_id = #{machineId}")
    int existsByMachineId(String machineId);


}
