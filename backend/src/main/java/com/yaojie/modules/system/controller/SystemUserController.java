package com.yaojie.modules.system.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.system.dto.UserCreateRequest;
import com.yaojie.modules.system.dto.UserPasswordResetRequest;
import com.yaojie.modules.system.dto.UserRoleAssignRequest;
import com.yaojie.modules.system.dto.UserStatusUpdateRequest;
import com.yaojie.modules.system.service.SystemUserService;
import com.yaojie.modules.system.vo.SysRoleVO;
import com.yaojie.modules.system.vo.SysUserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SystemUserController {

    private final SystemUserService systemUserService;

    public SystemUserController(SystemUserService systemUserService) {
        this.systemUserService = systemUserService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<List<SysUserVO>> listUsers() {
        return ApiResponse.success(systemUserService.listUsers());
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<List<SysRoleVO>> listRoles() {
        return ApiResponse.success(systemUserService.listRoles());
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<SysUserVO> createUser(
        @RequestBody UserCreateRequest request,
        Authentication authentication,
        HttpServletRequest httpServletRequest
    ) {
        return ApiResponse.success(
            "created",
            systemUserService.createUser(request, authentication.getName(), httpServletRequest.getRemoteAddr())
        );
    }

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<SysUserVO> assignRoles(
        @PathVariable Long id,
        @RequestBody UserRoleAssignRequest request,
        Authentication authentication,
        HttpServletRequest httpServletRequest
    ) {
        return ApiResponse.success(
            "updated",
            systemUserService.assignRoles(id, request.getRoleIds(), authentication.getName(), httpServletRequest.getRemoteAddr())
        );
    }

    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<SysUserVO> updateStatus(
        @PathVariable Long id,
        @RequestBody UserStatusUpdateRequest request,
        Authentication authentication,
        HttpServletRequest httpServletRequest
    ) {
        return ApiResponse.success(
            "updated",
            systemUserService.updateUserStatus(id, request.getStatus(), authentication.getName(), httpServletRequest.getRemoteAddr())
        );
    }

    @PutMapping("/users/{id}/reset-password")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Void> resetPassword(
        @PathVariable Long id,
        @RequestBody UserPasswordResetRequest request,
        Authentication authentication,
        HttpServletRequest httpServletRequest
    ) {
        systemUserService.resetPassword(id, request.getNewPassword(), authentication.getName(), httpServletRequest.getRemoteAddr());
        return ApiResponse.success("updated", null);
    }
}
