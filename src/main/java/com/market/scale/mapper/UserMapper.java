package com.market.scale.mapper;

import com.market.scale.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users ORDER BY id")
    List<User> findAll();

    @Insert("INSERT INTO users(username, password_hash, display_name, role, enabled, created_at, updated_at) " +
            "VALUES(#{username}, #{passwordHash}, #{displayName}, #{role}, #{enabled}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET display_name = #{displayName}, role = #{role}, enabled = #{enabled}, updated_at = NOW() WHERE id = #{id}")
    int update(User user);

    @Update("UPDATE users SET password_hash = #{passwordHash}, updated_at = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);
}
