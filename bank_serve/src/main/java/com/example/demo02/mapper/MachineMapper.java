package com.example.demo02.mapper;


import com.example.demo02.domain.Machine;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface MachineMapper {

    // 查询所有设备
    @Select("SELECT machine_id, location, status, water_add_switch, fill_up, " +
            "device_temperature, battery_level, latitude_and_longitude FROM machine")
    List<Machine> findAll();

    // 根据ID查询设备
    @Select("SELECT machine_id, location, status, water_add_switch, fill_up, " +
            "device_temperature, battery_level, latitude_and_longitude FROM machine " +
            "WHERE machine_id = #{machineId}")
    Machine findById(@Param("machineId") String machineId);

    // 根据状态查询设备
    @Select("SELECT machine_id, location, status, water_add_switch, fill_up, " +
            "device_temperature, battery_level, latitude_and_longitude FROM machine " +
            "WHERE status = #{status}")
    List<Machine> findByStatus(@Param("status") String status);

    // 根据位置模糊查询设备
    @Select("SELECT machine_id, location, status, water_add_switch, fill_up, " +
            "device_temperature, battery_level, latitude_and_longitude FROM machine " +
            "WHERE location LIKE CONCAT('%', #{location}, '%')")
    List<Machine> findByLocation(@Param("location") String location);

    // 插入设备
    @Insert("INSERT INTO machine (machine_id, location, status, water_add_switch, fill_up, " +
            "device_temperature, battery_level, latitude_and_longitude) " +
            "VALUES (#{machineId}, #{location}, #{status}, #{waterAddSwitch}, #{fillUp}, " +
            "#{deviceTemperature}, #{batteryLevel}, #{latitudeAndLongitude})")
    int insert(Machine machine);

    // 更新设备信息
    @Update("UPDATE machine SET location = #{location}, status = #{status}, " +
            "water_add_switch = #{waterAddSwitch}, fill_up = #{fillUp}, " +
            "device_temperature = #{deviceTemperature}, battery_level = #{batteryLevel}, " +
            "latitude_and_longitude = #{latitudeAndLongitude} " +
            "WHERE machine_id = #{machineId}")
    int update(Machine machine);


    // 更新设备实时数据（MQTT用）
    @Update("UPDATE machine SET water_add_switch = #{waterAddSwitch}, fill_up = #{fillUp}, " +
            "device_temperature = #{deviceTemperature}, battery_level = #{batteryLevel}, " +
            "latitude_and_longitude = #{latitudeAndLongitude} " +
            "WHERE machine_id = #{machineId}")
    int updateDeviceData(@Param("machineId") String machineId,
                         @Param("waterAddSwitch") String waterAddSwitch,
                         @Param("fillUp") String fillUp,
                         @Param("deviceTemperature") String deviceTemperature,
                         @Param("batteryLevel") String batteryLevel,
                         @Param("latitudeAndLongitude") String latitudeAndLongitude);

    // 删除设备
    @Delete("DELETE FROM machine WHERE machine_id = #{machineId}")
    int deleteById(@Param("machineId") String machineId);

    // 检查设备是否存在
    @Select("SELECT COUNT(*) FROM machine WHERE machine_id = #{machineId}")
    int existsByMachineId(@Param("machineId") String machineId);

    // 统计设备数量
    @Select("SELECT COUNT(*) FROM machine")
    int countAll();

    // 根据状态统计设备数量
    @Select("SELECT COUNT(*) FROM machine WHERE status = #{status}")
    int countByStatus(@Param("status") String status);

    // 增量更新设备（只更新非空字段）
    @Update("<script>" +
            "UPDATE machine SET " +
            "<if test='location != null'>location = #{location},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='waterAddSwitch != null'>water_add_switch = #{waterAddSwitch},</if>" +
            "<if test='fillUp != null'>fill_up = #{fillUp},</if>" +
            "<if test='deviceTemperature != null'>device_temperature = #{deviceTemperature},</if>" +
            "<if test='batteryLevel != null'>battery_level = #{batteryLevel},</if>" +
            "<if test='latitudeAndLongitude != null'>latitude_and_longitude = #{latitudeAndLongitude}</if>" +
            " WHERE machine_id = #{machineId}" +
            "</script>")
    int incrementalUpdate(Machine machine);

    // 分别更新每个字段的方法
    @Update("UPDATE machine SET water_add_switch = #{value} WHERE machine_id = #{machineId}")
    int updateWaterAddSwitch(@Param("machineId") String machineId, @Param("value") String value);

    @Update("UPDATE machine SET fill_up = #{value} WHERE machine_id = #{machineId}")
    int updateFillUp(@Param("machineId") String machineId, @Param("value") String value);

    @Update("UPDATE machine SET device_temperature = #{value} WHERE machine_id = #{machineId}")
    int updateDeviceTemperature(@Param("machineId") String machineId, @Param("value") String value);

    @Update("UPDATE machine SET battery_level = #{value} WHERE machine_id = #{machineId}")
    int updateBatteryLevel(@Param("machineId") String machineId, @Param("value") String value);

    @Update("UPDATE machine SET latitude_and_longitude = #{value} WHERE machine_id = #{machineId}")
    int updateLatitudeLongitude(@Param("machineId") String machineId, @Param("value") String value);

    @Update("UPDATE machine SET status = #{value} WHERE machine_id = #{machineId}")
    int updateStatus(@Param("machineId") String machineId, @Param("value") String value);

    @Update("UPDATE machine SET location = #{value} WHERE machine_id = #{machineId}")
    int updateLocation(@Param("machineId") String machineId, @Param("value") String value);


}
