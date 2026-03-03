package com.beeftracker.backend.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.beeftracker.backend.auth.models.user.User;
import com.beeftracker.backend.auth.models.user.UserData;
import com.beeftracker.backend.auth.repositories.UserRepository;

@Service
public class JwtService {
    private final UserRepository userRepository;
    private final Key key;

    public JwtService(UserRepository userRepository, @Value("${spring.secret}") String baseKey) {
        this.userRepository = userRepository;
        this.key = Keys.hmacShaKeyFor(baseKey.getBytes());
        
    }

    public String gerarToken(UserData loginForm) throws Exception {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = userRepository.findByDataEmail(loginForm.email());

        if (user == null) {
            throw new Exception();
        }

        if (!encoder.matches(loginForm.senha(), user.data().senha())) {
            throw new Exception();
        }

        return Jwts.builder()
                .setSubject(user.metadata().id().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validarToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}