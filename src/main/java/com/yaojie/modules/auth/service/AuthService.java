package com.yaojie.modules.auth.service;

import com.yaojie.modules.auth.dto.LoginRequest;
import com.yaojie.modules.auth.vo.LoginResponse;
import com.yaojie.modules.auth.vo.LoginUserVO;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginUserVO getCurrentUser(String username);
}
