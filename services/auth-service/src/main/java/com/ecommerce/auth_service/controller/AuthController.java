package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.dto.*;
import com.ecommerce.auth_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) {

        userService.register(request);

        return new ApiResponse<>(true, "Register success", null);
    }

    @PostMapping("/login")
    public ApiResponse<?> login(
            @RequestBody LoginRequest request) {

        AuthResponse token = userService.login(request);

        return new ApiResponse<>(
                true,
                "Login success",
                token
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse>
    refreshToken(
            @RequestBody
            RefreshTokenRequest request) {

        return ApiResponse.success(
                "Refresh token success",
                userService.refreshToken(request)
        );
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(
            HttpServletRequest httpServletRequest,
            @RequestBody LogoutRequest request
    ) {

        userService.logout(
                httpServletRequest,
                request
        );

        return ApiResponse.success(
                "Logout success",
                null
        );
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from auth-service";
    }
}