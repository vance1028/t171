package com.market.scale.mapper;

import com.market.scale.entity.ScaleStatusLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScaleStatusLogMapper {

    @Select("SELECT * FROM scale_status_logs WHERE scale_id = #{scaleId} ORDER BY operated_at DESC")
    List<ScaleStatusLog> findByScaleId(Long scaleId);

    @Select("SELECT * FROM scale_status_logs WHERE scale_id = #{scaleId} ORDER BY operated_at DESC LIMIT #{offset}, #{limit}")
    List<ScaleStatusLog> pageByScaleId(@Param("scaleId") Long scaleId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM scale_status_logs WHERE scale_id = #{scaleId}")
    long countByScaleId(Long scaleId);

    @Insert("INSERT INTO scale_status_logs(scale_id, from_status, to_status, reason, operated_by, operated_at) " +
            "VALUES(#{scaleId}, #{fromStatus}, #{toStatus}, #{reason}, #{operatedBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ScaleStatusLog log);

    @Select("SELECT COUNT(*) FROM scale_status_logs WHERE scale_id = #{scaleId} AND to_status = 'suspended' " +
            "AND (reason LIKE '%超期%' OR reason LIKE '%过期%' OR reason LIKE '%overdue%')")
    int countOverdueSuspensions(Long scaleId);
}
