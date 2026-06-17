package com.ecommerce.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Email(message = "Invalid email format")
    private String email;
}