package com.yaojie.modules.system.service.impl;

import com.yaojie.common.enums.ResultCode;
import com.yaojie.common.exception.BusinessException;
import com.yaojie.modules.audit.entity.OperationLog;
import com.yaojie.modules.audit.mapper.OperationLogMapper;
import com.yaojie.modules.system.entity.SysUser;
import com.yaojie.modules.system.mapper.SysRoleMapper;
import com.yaojie.modules.system.mapper.SysUserMapper;
import com.yaojie.modules.system.mapper.SysUserRoleMapper;
import com.yaojie.modules.system.service.SystemUserService;
import com.yaojie.modules.system.vo.SysRoleVO;
import com.yaojie.modules.system.vo.SysUserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SystemUserServiceImpl implements SystemUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final OperationLogMapper operationLogMapper;

    public SystemUserServiceImpl(
        SysUserMapper sysUserMapper,
        SysRoleMapper sysRoleMapper,
        SysUserRoleMapper sysUserRoleMapper,
        OperationLogMapper operationLogMapper
    ) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public List<SysUserVO> listUsers() {
        List<SysUserVO> users = sysUserMapper.selectList();
        users.forEach(this::fillUserRoles);
        return users;
    }

    @Override
    public List<SysRoleVO> listRoles() {
        return sysRoleMapper.selectList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserVO assignRoles(Long userId, List<Long> roleIds, String operatorUsername, String ip) {
        SysUser targetUser = sysUserMapper.selectById(userId);
        if (targetUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        List<Long> safeRoleIds = roleIds == null ? Collections.emptyList() : roleIds;
        List<SysRoleVO> selectedRoles = safeRoleIds.isEmpty() ? Collections.emptyList() : sysRoleMapper.selectByIds(safeRoleIds);
        if (selectedRoles.size() != safeRoleIds.size()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Role selection is invalid");
        }

        sysUserRoleMapper.deleteByUserId(userId);
        if (!safeRoleIds.isEmpty()) {
            sysUserRoleMapper.batchInsert(userId, safeRoleIds);
        }

        SysUser operator = sysUserMapper.selectByUsername(operatorUsername);
        OperationLog log = new OperationLog();
        log.setUserId(operator == null ? null : operator.getId());
        log.setModuleName("SYSTEM");
        log.setOperationType("ASSIGN_ROLE");
        log.setBusinessNo(String.valueOf(userId));
        log.setContent("Assign roles for user " + targetUser.getUsername());
        log.setIp(ip);
        operationLogMapper.insert(log);

        SysUserVO userVO = sysUserMapper.selectUserViewById(userId);
        fillUserRoles(userVO);
        return userVO;
    }

    private void fillUserRoles(SysUserVO user) {
        if (user == null) {
            return;
        }

        List<SysRoleVO> roles = sysUserMapper.selectRolesByUserId(user.getId());
        List<Long> roleIds = new ArrayList<>();
        List<String> roleCodes = new ArrayList<>();
        List<String> roleNames = new ArrayList<>();

        for (SysRoleVO role : roles) {
            roleIds.add(role.getId());
            roleCodes.add(role.getRoleCode());
            roleNames.add(role.getRoleName());
        }

        user.setRoleIds(roleIds);
        user.setRoleCodes(roleCodes);
        user.setRoleNames(roleNames);
    }
}
