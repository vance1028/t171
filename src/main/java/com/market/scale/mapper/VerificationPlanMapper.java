package com.market.scale.mapper;

import com.market.scale.entity.VerificationPlan;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VerificationPlanMapper {

    @Select("SELECT * FROM verification_plans WHERE id = #{id}")
    VerificationPlan findById(Long id);

    @Select("SELECT * FROM verification_plans WHERE plan_no = #{planNo}")
    VerificationPlan findByPlanNo(String planNo);

    @Select("<script>" +
            "SELECT * FROM verification_plans " +
            "<where>" +
            "  <if test='marketName != null and marketName != \"\"'>AND market_name = #{marketName}</if>" +
            "  <if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "</where>" +
            " ORDER BY id DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<VerificationPlan> search(@Param("marketName") String marketName,
                                  @Param("status") String status,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM verification_plans " +
            "<where>" +
            "  <if test='marketName != null and marketName != \"\"'>AND market_name = #{marketName}</if>" +
            "  <if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "</where>" +
            "</script>")
    long count(@Param("marketName") String marketName, @Param("status") String status);

    @Insert("INSERT INTO verification_plans(plan_no, market_name, status, total_count, assigned_org, assigned_person, remark, created_by, created_at, updated_at) " +
            "VALUES(#{planNo}, #{marketName}, #{status}, #{totalCount}, #{assignedOrg}, #{assignedPerson}, #{remark}, #{createdBy}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VerificationPlan plan);

    @Update("UPDATE verification_plans SET market_name = #{marketName}, status = #{status}, total_count = #{totalCount}, " +
            "assigned_org = #{assignedOrg}, assigned_person = #{assignedPerson}, remark = #{remark}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(VerificationPlan plan);
}
