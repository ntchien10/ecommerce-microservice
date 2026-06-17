package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.dto.*;
import com.ecommerce.auth_service.entity.RefreshToken;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.RefreshTokenRepository;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.service.UserService;
import com.ecommerce.auth_service.service.redis.TokenBlacklistService;
import com.ecommerce.auth_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, com.ecommerce.auth_service.util.JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, TokenBlacklistService tokenBlacklistService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void register(RegisterRequest request) {

        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setEmail(request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(
                request.getUsername()
        ).orElseThrow(() ->
                new RuntimeException("Username not found")
        );

        boolean isMatch = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        String accessToken = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        String refreshToken =
                jwtUtil.generateRefreshToken(
                        user.getUsername()
                );

        RefreshToken refreshTokenEntity =
                new RefreshToken();

        refreshTokenEntity.setToken(refreshToken);

        refreshTokenEntity.setUsername(
                user.getUsername()
        );

        refreshTokenEntity.setExpiryDate(
                LocalDateTime.now().plusDays(7)
        );

        refreshTokenRepository.save(
                refreshTokenEntity
        );

        return new AuthResponse(
                accessToken,
                refreshToken
        );

    }

    @Override
    public AuthResponse refreshToken(
            RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(
                                request.getRefreshToken()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"
                                )
                        );

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Refresh token expired"
            );
        }

        User user = userRepository
                .findByUsername(
                        refreshToken.getUsername()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        String newAccessToken =
                jwtUtil.generateToken(
                        user.getUsername(),
                        user.getRole().name()
                );

        return new AuthResponse(
                newAccessToken,
                refreshToken.getToken()
        );
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest httpServletRequest, LogoutRequest request )
    {
        refreshTokenRepository .deleteByToken( request.getRefreshToken() );
        String authHeader = httpServletRequest.getHeader( "Authorization" );
        if (authHeader != null && authHeader.startsWith("Bearer "))
        { String accessToken = authHeader.substring(7);
            tokenBlacklistService .blacklistToken(accessToken);
        }
    }
}