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

    @Select("<script>" +
            "SELECT * FROM scales " +
            "<where>" +
            "  <if test='stallId != null'>AND stall_id = #{stallId}</if>" +
            "  <if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "  <if test='marketName != null and marketName != \"\"'>AND stall_id IN (SELECT id FROM stalls WHERE market_name = #{marketName})</if>" +
            "</where>" +
            " ORDER BY id DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Scale> search(@Param("stallId") Long stallId,
                       @Param("status") String status,
                       @Param("marketName") String marketName,
                       @Param("offset") int offset,
                       @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM scales " +
            "<where>" +
            "  <if test='stallId != null'>AND stall_id = #{stallId}</if>" +
            "  <if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "  <if test='marketName != null and marketName != \"\"'>AND stall_id IN (SELECT id FROM stalls WHERE market_name = #{marketName})</if>" +
            "</where>" +
            "</script>")
    long countSearch(@Param("stallId") Long stallId,
                     @Param("status") String status,
                     @Param("marketName") String marketName);

    @Select("SELECT COUNT(*) FROM scales")
    long count();

    @Select("SELECT * FROM scales WHERE next_verify_date IS NOT NULL AND next_verify_date <= DATE_ADD(CURDATE(), INTERVAL #{warnDays} DAY) AND status IN ('in_use', 'pending_verify') ORDER BY next_verify_date")
    List<Scale> findExpiringSoon(@Param("warnDays") int warnDays);

    @Select("SELECT * FROM scales WHERE next_verify_date IS NOT NULL AND next_verify_date < CURDATE() AND status IN ('in_use', 'pending_verify') ORDER BY next_verify_date")
    List<Scale> findOverdue();

    @Insert("INSERT INTO scales(stall_id, asset_no, model, manufacturer, max_capacity_g, verified_at, verify_cycle_days, next_verify_date, current_seal_no, status, created_at, updated_at) " +
            "VALUES(#{stallId}, #{assetNo}, #{model}, #{manufacturer}, #{maxCapacityG}, #{verifiedAt}, #{verifyCycleDays}, #{nextVerifyDate}, #{currentSealNo}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Scale scale);

    @Update("UPDATE scales SET model = #{model}, manufacturer = #{manufacturer}, max_capacity_g = #{maxCapacityG}, " +
            "verified_at = #{verifiedAt}, verify_cycle_days = #{verifyCycleDays}, next_verify_date = #{nextVerifyDate}, " +
            "current_seal_no = #{currentSealNo}, status = #{status}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(Scale scale);

    @Update("UPDATE scales SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM scales WHERE id = #{id}")
    int delete(Long id);
}
