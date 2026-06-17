package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(
            HttpServletRequest httpServletRequest,
            LogoutRequest request
    );
}