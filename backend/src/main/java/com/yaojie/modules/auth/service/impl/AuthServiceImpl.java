package com.yaojie.modules.auth.service.impl;

import com.yaojie.common.enums.ResultCode;
import com.yaojie.common.exception.BusinessException;
import com.yaojie.common.utils.PasswordUtil;
import com.yaojie.modules.auth.dto.LoginRequest;
import com.yaojie.modules.auth.service.AuthService;
import com.yaojie.modules.auth.vo.LoginResponse;
import com.yaojie.modules.auth.vo.LoginUserVO;
import com.yaojie.modules.system.entity.SysUser;
import com.yaojie.modules.system.mapper.SysUserMapper;
import com.yaojie.security.JwtProperties;
import com.yaojie.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public AuthServiceImpl(SysUserMapper sysUserMapper, JwtTokenProvider jwtTokenProvider, JwtProperties jwtProperties) {
        this.sysUserMapper = sysUserMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectByUsername(request.getUsername());
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }
        if (!PasswordUtil.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        List<String> roleCodes = sysUserMapper.selectRoleCodesByUserId(user.getId());
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), roleCodes);

        return LoginResponse.builder()
            .accessToken(token)
            .tokenType("Bearer")
            .expiresIn(jwtProperties.getExpireSeconds())
            .user(toLoginUserVO(user, roleCodes))
            .build();
    }

    @Override
    public LoginUserVO getCurrentUser(String username) {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toLoginUserVO(user, sysUserMapper.selectRoleCodesByUserId(user.getId()));
    }

    private LoginUserVO toLoginUserVO(SysUser user, List<String> roleCodes) {
        return LoginUserVO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .realName(user.getRealName())
            .roles(roleCodes)
            .build();
    }
}
