package com.market.scale.mapper;

import com.market.scale.entity.RecheckRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecheckRecordMapper {

    @Select("SELECT * FROM recheck_records WHERE id = #{id}")
    RecheckRecord findById(Long id);

    @Select("<script>" +
            "SELECT * FROM recheck_records " +
            "<where>" +
            "  <if test='stallId != null'>AND stall_id = #{stallId}</if>" +
            "  <if test='result != null and result != \"\"'>AND result = #{result}</if>" +
            "</where>" +
            " ORDER BY rechecked_at DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<RecheckRecord> search(@Param("stallId") Long stallId, @Param("result") String result,
                               @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM recheck_records " +
            "<where>" +
            "  <if test='stallId != null'>AND stall_id = #{stallId}</if>" +
            "  <if test='result != null and result != \"\"'>AND result = #{result}</if>" +
            "</where>" +
            "</script>")
    long count(@Param("stallId") Long stallId, @Param("result") String result);

    @Insert("INSERT INTO recheck_records(stall_id, commodity, claimed_weight_g, actual_weight_g, shortage_g, result, handled_by, remark, rechecked_at, created_at) " +
            "VALUES(#{stallId}, #{commodity}, #{claimedWeightG}, #{actualWeightG}, #{shortageG}, #{result}, #{handledBy}, #{remark}, #{recheckedAt}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RecheckRecord record);
}
