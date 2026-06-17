package com.ecommerce.product_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/auth/hello")
    String hello();
}