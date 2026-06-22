package com.market.scale.mapper;

import com.market.scale.entity.VerificationPlanItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VerificationPlanItemMapper {

    @Select("SELECT * FROM verification_plan_items WHERE id = #{id}")
    VerificationPlanItem findById(Long id);

    @Select("SELECT * FROM verification_plan_items WHERE plan_id = #{planId} ORDER BY id")
    List<VerificationPlanItem> findByPlanId(Long planId);

    @Select("SELECT * FROM verification_plan_items WHERE scale_id = #{scaleId} ORDER BY id DESC")
    List<VerificationPlanItem> findByScaleId(Long scaleId);

    @Insert("INSERT INTO verification_plan_items(plan_id, scale_id, asset_no, result, fail_reason, created_at) " +
            "VALUES(#{planId}, #{scaleId}, #{assetNo}, #{result}, #{failReason}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VerificationPlanItem item);

    @Insert("<script>" +
            "INSERT INTO verification_plan_items(plan_id, scale_id, asset_no, result, created_at) VALUES " +
            "<foreach collection='items' item='item' separator=','>" +
            "(#{item.planId}, #{item.scaleId}, #{item.assetNo}, #{item.result}, NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("items") List<VerificationPlanItem> items);

    @Update("UPDATE verification_plan_items SET result = #{result}, fail_reason = #{failReason} WHERE id = #{id}")
    int updateResult(VerificationPlanItem item);
}
