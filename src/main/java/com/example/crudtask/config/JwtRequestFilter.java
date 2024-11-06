package com.example.crudtask.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);  // Извлекаем токен из заголовков запроса
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUtil.extractUsername(token), null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);  // Устанавливаем пользователя в контекст безопасности
        }
        filterChain.doFilter(request, response);  // Пропускаем запрос дальше
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);  // Убираем "Bearer " из заголовка
        }
        return null;
    }
}