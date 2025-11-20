package com.example.demo02.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo02.domain.Machine;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MachineMapper extends BaseMapper<Machine> {

    // 查询所有设备
    @Select("SELECT * FROM machine")
    List<Machine> findAll();

    // 根据设备ID查询设备
    @Select("SELECT * FROM machine WHERE machine_id = #{machineId}")
    Machine findByMachineId(@Param("machineId") String machineId);

    // 插入设备
    @Insert("INSERT INTO machine (machine_id, location, status, water_add_switch, pause, enable_device, " +
            "water_tank, fill_up, device_temperature, battery_level, total_water_addition, latitude, longitude, there_fee) " +
            "VALUES (#{machineId}, #{location}, #{status}, #{waterAddSwitch}, #{pause}, #{enableDevice}, " +
            "#{waterTank}, #{fillUp}, #{deviceTemperature}, #{batteryLevel}, #{totalWaterAddition}, #{latitude}, #{longitude}, #{thereFee})")
    int insert(Machine machine);

    // 更新设备信息（全字段更新）
    @Update("UPDATE machine SET location = #{location}, status = #{status}, water_add_switch = #{waterAddSwitch}, " +
            "pause = #{pause}, enable_device = #{enableDevice}, water_tank = #{waterTank}, fill_up = #{fillUp}, " +
            "device_temperature = #{deviceTemperature}, battery_level = #{batteryLevel}, total_water_addition = #{totalWaterAddition}, " +
            "latitude = #{latitude}, longitude = #{longitude}, there_fee = #{thereFee} WHERE machine_id = #{machineId}")
    int update(Machine machine);

    // 删除设备
    @Delete("DELETE FROM machine WHERE machine_id = #{machineId}")
    int deleteById(@Param("machineId") String machineId);

    // 检查设备是否存在
    @Select("SELECT COUNT(*) FROM machine WHERE machine_id = #{machineId}")
    int existsByMachineId(@Param("machineId") String machineId);

    // ========== 单个字段更新方法 ==========

    @Update("UPDATE machine SET location = #{location} WHERE machine_id = #{machineId}")
    int updateLocation(@Param("machineId") String machineId, @Param("location") String location);

    @Update("UPDATE machine SET status = #{status} WHERE machine_id = #{machineId}")
    int updateStatus(@Param("machineId") String machineId, @Param("status") String status);

    @Update("UPDATE machine SET water_add_switch = #{waterAddSwitch} WHERE machine_id = #{machineId}")
    int updateWaterAddSwitch(@Param("machineId") String machineId, @Param("waterAddSwitch") String waterAddSwitch);

    @Update("UPDATE machine SET pause = #{pause} WHERE machine_id = #{machineId}")
    int updatePause(@Param("machineId") String machineId, @Param("pause") String pause);

    @Update("UPDATE machine SET enable_device = #{enableDevice} WHERE machine_id = #{machineId}")
    int updateEnableDevice(@Param("machineId") String machineId, @Param("enableDevice") String enableDevice);

    @Update("UPDATE machine SET water_tank = #{waterTank} WHERE machine_id = #{machineId}")
    int updateWaterTank(@Param("machineId") String machineId, @Param("waterTank") String waterTank);

    @Update("UPDATE machine SET fill_up = #{fillUp} WHERE machine_id = #{machineId}")
    int updateFillUp(@Param("machineId") String machineId, @Param("fillUp") String fillUp);

    @Update("UPDATE machine SET device_temperature = #{deviceTemperature} WHERE machine_id = #{machineId}")
    int updateDeviceTemperature(@Param("machineId") String machineId, @Param("deviceTemperature") String deviceTemperature);

    @Update("UPDATE machine SET battery_level = #{batteryLevel} WHERE machine_id = #{machineId}")
    int updateBatteryLevel(@Param("machineId") String machineId, @Param("batteryLevel") String batteryLevel);

    @Update("UPDATE machine SET total_water_addition = #{totalWaterAddition} WHERE machine_id = #{machineId}")
    int updateTotalWaterAddition(@Param("machineId") String machineId, @Param("totalWaterAddition") Double totalWaterAddition);

    @Update("UPDATE machine SET latitude = #{latitude} WHERE machine_id = #{machineId}")
    int updateLatitude(@Param("machineId") String machineId, @Param("latitude") String latitude);

    @Update("UPDATE machine SET longitude = #{longitude} WHERE machine_id = #{machineId}")
    int updateLongitude(@Param("machineId") String machineId, @Param("longitude") String longitude);

    @Update("UPDATE machine SET there_fee = #{thereFee} WHERE machine_id = #{machineId}")
    int updateThereFee(@Param("machineId") String machineId, @Param("thereFee") String thereFee);

    // 增加总加水量
    @Update("UPDATE machine SET total_water_addition = total_water_addition + #{addition} WHERE machine_id = #{machineId}")
    int addTotalWaterAddition(@Param("machineId") String machineId, @Param("addition") Double addition);

    // 统计设备数量
    @Select("SELECT COUNT(*) FROM machine")
    int countAll();

    // 根据状态查询设备
    @Select("SELECT * FROM machine WHERE status = #{status}")
    List<Machine> findByStatus(@Param("status") String status);

    // 根据启用状态查询设备
    @Select("SELECT * FROM machine WHERE enable_device = #{enableDevice}")
    List<Machine> findByEnableDevice(@Param("enableDevice") String enableDevice);

    // 查询没有费率配置的设备
    @Select("SELECT m.* FROM machine m LEFT JOIN rate_config rc ON m.machine_id = rc.machine_id WHERE rc.rate_id IS NULL")
    List<Machine> findMachinesWithoutRateConfig();

    // 根据费率标识查询设备
    @Select("SELECT * FROM machine WHERE there_fee = #{thereFee}")
    List<Machine> findByThereFee(@Param("thereFee") String thereFee);

    // 查询没有费率配置的设备数量
    @Select("SELECT COUNT(*) FROM machine m LEFT JOIN rate_config rc ON m.machine_id = rc.machine_id WHERE rc.rate_id IS NULL")
    int countMachinesWithoutRateConfig();


}