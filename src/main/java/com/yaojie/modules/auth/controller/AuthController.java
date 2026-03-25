package com.yaojie.modules.auth.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.auth.dto.LoginRequest;
import com.yaojie.modules.auth.service.AuthService;
import com.yaojie.modules.auth.vo.LoginResponse;
import com.yaojie.modules.auth.vo.LoginUserVO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<LoginUserVO> currentUser(Authentication authentication) {
        return ApiResponse.success(authService.getCurrentUser(authentication.getName()));
    }
}
