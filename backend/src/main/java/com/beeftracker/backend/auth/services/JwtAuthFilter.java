package com.beeftracker.backend.auth.services;

import java.io.IOException;

import com.beeftracker.backend.base.exceptions.GlobalExceptionHandler;
import com.beeftracker.backend.base.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;


    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/public");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) {



        try{
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new UnauthorizedException();
            }

            String token = header.substring(7);
            String id = jwtService.validarToken(token);
            request.setAttribute("userId", id);
            filterChain.doFilter(request, response);
        }catch (Exception e){
            resolver.resolveException(request, response, null, new UnauthorizedException());
        }
    }

}
