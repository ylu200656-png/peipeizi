package com.yaojie.modules.system.service;

import com.yaojie.modules.system.dto.UserCreateRequest;
import com.yaojie.modules.system.vo.SysRoleVO;
import com.yaojie.modules.system.vo.SysUserVO;

import java.util.List;

public interface SystemUserService {

    List<SysUserVO> listUsers();

    List<SysRoleVO> listRoles();

    SysUserVO createUser(UserCreateRequest request, String operatorUsername, String ip);

    SysUserVO assignRoles(Long userId, List<Long> roleIds, String operatorUsername, String ip);

    SysUserVO updateUserStatus(Long userId, Integer status, String operatorUsername, String ip);

    void resetPassword(Long userId, String newPassword, String operatorUsername, String ip);
}
