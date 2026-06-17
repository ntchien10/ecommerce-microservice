package com.ecommerce.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {

    private String refreshToken;
}