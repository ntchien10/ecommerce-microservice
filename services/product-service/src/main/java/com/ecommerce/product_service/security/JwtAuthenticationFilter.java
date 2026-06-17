package com.ecommerce.product_service.security;

import com.ecommerce.product_service.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil
    ) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader =
                request.getHeader(
                        "Authorization"
                );

        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {

            String token =
                    authHeader.substring(7);

            String username =
                    jwtUtil.extractUsername(token);

            String role =
                    jwtUtil.extractRole(token);

            System.out.println("USERNAME = " + username);
            System.out.println("ROLE = " + role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            role
                                    )
                            )
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );
            System.out.println(
                    SecurityContextHolder.getContext().getAuthentication()
            );
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}
