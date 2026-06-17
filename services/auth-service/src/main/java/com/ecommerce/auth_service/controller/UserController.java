package com.ecommerce.auth_service.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/profile")
    public String profile(Authentication authentication) {

        return "Hello " + authentication.getName();
    }
}