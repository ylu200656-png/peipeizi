package com.yaojie.modules.system.mapper;

import com.yaojie.modules.system.entity.SysUser;
import com.yaojie.modules.system.vo.SysRoleVO;
import com.yaojie.modules.system.vo.SysUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {

    SysUser selectByUsername(@Param("username") String username);

    SysUser selectById(@Param("id") Long id);

    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    List<SysUserVO> selectList();

    SysUserVO selectUserViewById(@Param("id") Long id);

    List<SysRoleVO> selectRolesByUserId(@Param("userId") Long userId);
}
