package com.yaojie.modules.system.service;

import com.yaojie.modules.system.vo.SysRoleVO;
import com.yaojie.modules.system.vo.SysUserVO;

import java.util.List;

public interface SystemUserService {

    List<SysUserVO> listUsers();

    List<SysRoleVO> listRoles();

    SysUserVO assignRoles(Long userId, List<Long> roleIds, String operatorUsername, String ip);
}
