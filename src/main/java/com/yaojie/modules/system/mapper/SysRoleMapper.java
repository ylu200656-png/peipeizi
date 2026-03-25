package com.yaojie.modules.system.mapper;

import com.yaojie.modules.system.vo.SysRoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    List<SysRoleVO> selectList();

    List<SysRoleVO> selectByIds(@Param("roleIds") List<Long> roleIds);
}
