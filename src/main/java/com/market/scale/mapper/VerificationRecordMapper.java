package com.market.scale.mapper;

import com.market.scale.entity.VerificationRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VerificationRecordMapper {

    @Select("SELECT * FROM verification_records WHERE id = #{id}")
    VerificationRecord findById(Long id);

    @Select("SELECT * FROM verification_records WHERE scale_id = #{scaleId} ORDER BY verified_at DESC")
    List<VerificationRecord> findByScaleId(Long scaleId);

    @Select("SELECT * FROM verification_records WHERE plan_id = #{planId} ORDER BY verified_at DESC")
    List<VerificationRecord> findByPlanId(Long planId);

    @Select("<script>" +
            "SELECT * FROM verification_records " +
            "<where>" +
            "  <if test='scaleId != null'>AND scale_id = #{scaleId}</if>" +
            "  <if test='conclusion != null and conclusion != \"\"'>AND conclusion = #{conclusion}</if>" +
            "</where>" +
            " ORDER BY verified_at DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<VerificationRecord> search(@Param("scaleId") Long scaleId,
                                     @Param("conclusion") String conclusion,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM verification_records " +
            "<where>" +
            "  <if test='scaleId != null'>AND scale_id = #{scaleId}</if>" +
            "  <if test='conclusion != null and conclusion != \"\"'>AND conclusion = #{conclusion}</if>" +
            "</where>" +
            "</script>")
    long count(@Param("scaleId") Long scaleId, @Param("conclusion") String conclusion);

    @Insert("INSERT INTO verification_records(scale_id, plan_id, verify_org, verify_person, conclusion, cert_no, seal_no, " +
            "verified_at, next_verify_date, valid_until, limited_use_days, remark, created_at) " +
            "VALUES(#{scaleId}, #{planId}, #{verifyOrg}, #{verifyPerson}, #{conclusion}, #{certNo}, #{sealNo}, " +
            "#{verifiedAt}, #{nextVerifyDate}, #{validUntil}, #{limitedUseDays}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VerificationRecord record);

    @Select("SELECT COUNT(*) FROM verification_records WHERE scale_id = #{scaleId}")
    int countByScaleId(Long scaleId);
}
