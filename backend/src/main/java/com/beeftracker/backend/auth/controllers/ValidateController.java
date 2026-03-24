package com.beeftracker.backend.auth.controllers;

import com.beeftracker.backend.usuarios.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth/validate")
public class ValidateController {
    public final UsuarioService usuarioService;

    public ValidateController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<?> teste(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new UserId(userId));
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles(@RequestAttribute("userId") String userId) throws Exception {
        return ResponseEntity.ok(usuarioService.getRoles(Long.valueOf(userId)));
    }

}

record UserId(String id){}
