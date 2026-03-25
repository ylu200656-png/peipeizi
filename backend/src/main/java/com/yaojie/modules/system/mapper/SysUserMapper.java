package com.yaojie.modules.system.mapper;

import com.yaojie.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {

    SysUser selectByUsername(@Param("username") String username);

    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
