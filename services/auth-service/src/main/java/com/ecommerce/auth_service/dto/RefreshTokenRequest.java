package com.ecommerce.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    private String refreshToken;
}