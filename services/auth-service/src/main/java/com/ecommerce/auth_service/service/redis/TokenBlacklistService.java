package com.ecommerce.auth_service.service.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    public TokenBlacklistService(
            StringRedisTemplate redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String token) {

        System.out.println(
                "BLACKLIST TOKEN: " + token
        );

        redisTemplate.opsForValue().set(
                token,
                "blacklisted",
                1,
                TimeUnit.DAYS
        );
    }

    public boolean isBlacklisted(String token) {

        return redisTemplate.hasKey(token);
    }
}