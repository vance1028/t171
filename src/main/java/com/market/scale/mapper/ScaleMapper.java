package com.market.scale.mapper;

import com.market.scale.entity.Scale;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScaleMapper {

    @Select("SELECT * FROM scales WHERE id = #{id}")
    Scale findById(Long id);

    @Select("SELECT * FROM scales WHERE asset_no = #{assetNo}")
    Scale findByAssetNo(String assetNo);

    @Select("SELECT * FROM scales WHERE stall_id = #{stallId} ORDER BY id")
    List<Scale> findByStall(Long stallId);

    @Select("SELECT * FROM scales ORDER BY id DESC LIMIT #{offset}, #{limit}")
    List<Scale> findPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM scales")
    long count();

    @Insert("INSERT INTO scales(stall_id, asset_no, model, manufacturer, max_capacity_g, verified_at, verify_cycle_days, status, created_at, updated_at) " +
            "VALUES(#{stallId}, #{assetNo}, #{model}, #{manufacturer}, #{maxCapacityG}, #{verifiedAt}, #{verifyCycleDays}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Scale scale);

    @Update("UPDATE scales SET model = #{model}, manufacturer = #{manufacturer}, max_capacity_g = #{maxCapacityG}, " +
            "verified_at = #{verifiedAt}, verify_cycle_days = #{verifyCycleDays}, status = #{status}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(Scale scale);

    @Delete("DELETE FROM scales WHERE id = #{id}")
    int delete(Long id);
}
