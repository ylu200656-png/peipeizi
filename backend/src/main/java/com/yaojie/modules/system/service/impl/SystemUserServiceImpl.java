package com.yaojie.modules.system.service.impl;

import com.yaojie.common.enums.ResultCode;
import com.yaojie.common.exception.BusinessException;
import com.yaojie.common.utils.PasswordUtil;
import com.yaojie.modules.audit.entity.OperationLog;
import com.yaojie.modules.audit.mapper.OperationLogMapper;
import com.yaojie.modules.system.dto.UserCreateRequest;
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
    public SysUserVO createUser(UserCreateRequest request, String operatorUsername, String ip) {
        String username = trimToNull(request.getUsername());
        String realName = trimToNull(request.getRealName());
        String password = trimToNull(request.getPassword());
        Integer status = request.getStatus() == null ? 1 : request.getStatus();

        if (username == null || realName == null || password == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Username, real name and password are required");
        }
        if (status != 0 && status != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Status is invalid");
        }
        if (sysUserMapper.countByUsername(username) > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        List<Long> safeRoleIds = request.getRoleIds() == null ? Collections.emptyList() : request.getRoleIds();
        List<SysRoleVO> selectedRoles = validateRoleSelection(safeRoleIds);

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setRealName(realName);
        user.setPasswordHash(PasswordUtil.encode(password));
        user.setStatus(status);
        sysUserMapper.insert(user);

        if (!safeRoleIds.isEmpty()) {
            sysUserRoleMapper.batchInsert(user.getId(), safeRoleIds);
        }

        SysUser operator = sysUserMapper.selectByUsername(operatorUsername);
        insertLog(operator, "SYSTEM", "CREATE_USER", String.valueOf(user.getId()), "Create user " + username, ip);

        SysUserVO userVO = sysUserMapper.selectUserViewById(user.getId());
        fillUserRoles(userVO);
        return userVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserVO assignRoles(Long userId, List<Long> roleIds, String operatorUsername, String ip) {
        SysUser targetUser = requireUser(userId);
        List<Long> safeRoleIds = roleIds == null ? Collections.emptyList() : roleIds;
        List<SysRoleVO> selectedRoles = validateRoleSelection(safeRoleIds);
        SysUser operator = sysUserMapper.selectByUsername(operatorUsername);

        validateAdminProtection(userId, targetUser.getStatus(), selectedRoles, operator);

        sysUserRoleMapper.deleteByUserId(userId);
        if (!safeRoleIds.isEmpty()) {
            sysUserRoleMapper.batchInsert(userId, safeRoleIds);
        }

        insertLog(operator, "SYSTEM", "ASSIGN_ROLE", String.valueOf(userId), "Assign roles for user " + targetUser.getUsername(), ip);

        SysUserVO userVO = sysUserMapper.selectUserViewById(userId);
        fillUserRoles(userVO);
        return userVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserVO updateUserStatus(Long userId, Integer status, String operatorUsername, String ip) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Status is invalid");
        }

        SysUser targetUser = requireUser(userId);
        if (targetUser.getStatus() != null && targetUser.getStatus().equals(status)) {
            SysUserVO userVO = sysUserMapper.selectUserViewById(userId);
            fillUserRoles(userVO);
            return userVO;
        }

        SysUser operator = sysUserMapper.selectByUsername(operatorUsername);
        if (status == 0) {
            validateAdminProtection(userId, targetUser.getStatus(), Collections.emptyList(), operator);
        }

        sysUserMapper.updateStatus(userId, status);
        insertLog(
            operator,
            "SYSTEM",
            status == 1 ? "ENABLE_USER" : "DISABLE_USER",
            String.valueOf(userId),
            (status == 1 ? "Enable user " : "Disable user ") + targetUser.getUsername(),
            ip
        );

        SysUserVO userVO = sysUserMapper.selectUserViewById(userId);
        fillUserRoles(userVO);
        return userVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String newPassword, String operatorUsername, String ip) {
        SysUser targetUser = requireUser(userId);
        String safePassword = trimToNull(newPassword);
        if (safePassword == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "New password is required");
        }

        sysUserMapper.updatePasswordHash(userId, PasswordUtil.encode(safePassword));
        SysUser operator = sysUserMapper.selectByUsername(operatorUsername);
        insertLog(operator, "SYSTEM", "RESET_PASSWORD", String.valueOf(userId), "Reset password for user " + targetUser.getUsername(), ip);
    }

    private SysUser requireUser(Long userId) {
        SysUser targetUser = sysUserMapper.selectById(userId);
        if (targetUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return targetUser;
    }

    private List<SysRoleVO> validateRoleSelection(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysRoleVO> selectedRoles = sysRoleMapper.selectByIds(roleIds);
        if (selectedRoles.size() != roleIds.size()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Role selection is invalid");
        }
        return selectedRoles;
    }

    private void validateAdminProtection(Long userId, Integer targetStatus, List<SysRoleVO> selectedRoles, SysUser operator) {
        if (targetStatus == null || targetStatus != 1) {
            return;
        }

        List<String> currentRoleCodes = sysUserMapper.selectRoleCodesByUserId(userId);
        boolean targetCurrentlyAdmin = currentRoleCodes.contains("ADMIN");
        boolean targetWillRemainAdmin = selectedRoles.stream().anyMatch(role -> "ADMIN".equals(role.getRoleCode()));

        if (targetCurrentlyAdmin && !targetWillRemainAdmin) {
            int adminCount = sysUserMapper.countUsersByRoleCode("ADMIN");
            if (adminCount <= 1) {
                throw new BusinessException(ResultCode.ROLE_ASSIGNMENT_INVALID, "At least one active admin must remain");
            }
            if (operator != null && operator.getId().equals(userId)) {
                throw new BusinessException(ResultCode.ROLE_ASSIGNMENT_INVALID, "Current admin cannot remove own admin role");
            }
        }
    }

    private void insertLog(SysUser operator, String moduleName, String operationType, String businessNo, String content, String ip) {
        OperationLog log = new OperationLog();
        log.setUserId(operator == null ? null : operator.getId());
        log.setModuleName(moduleName);
        log.setOperationType(operationType);
        log.setBusinessNo(businessNo);
        log.setContent(content);
        log.setIp(ip);
        operationLogMapper.insert(log);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
