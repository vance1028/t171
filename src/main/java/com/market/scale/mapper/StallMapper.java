package com.market.scale.mapper;

import com.market.scale.entity.Stall;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StallMapper {

    @Select("SELECT * FROM stalls WHERE id = #{id}")
    Stall findById(Long id);

    @Select("SELECT * FROM stalls WHERE stall_no = #{stallNo}")
    Stall findByStallNo(String stallNo);

    @Select("<script>" +
            "SELECT * FROM stalls " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (stall_no LIKE CONCAT('%', #{keyword}, '%') OR merchant_name LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "  <if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "</where>" +
            " ORDER BY id DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Stall> search(@Param("keyword") String keyword, @Param("status") String status,
                       @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM stalls " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (stall_no LIKE CONCAT('%', #{keyword}, '%') OR merchant_name LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "  <if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "</where>" +
            "</script>")
    long count(@Param("keyword") String keyword, @Param("status") String status);

    @Insert("INSERT INTO stalls(stall_no, market_name, merchant_name, category, contact_phone, status, created_at, updated_at) " +
            "VALUES(#{stallNo}, #{marketName}, #{merchantName}, #{category}, #{contactPhone}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Stall stall);

    @Update("UPDATE stalls SET market_name = #{marketName}, merchant_name = #{merchantName}, " +
            "category = #{category}, contact_phone = #{contactPhone}, status = #{status}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(Stall stall);

    @Delete("DELETE FROM stalls WHERE id = #{id}")
    int delete(Long id);
}
