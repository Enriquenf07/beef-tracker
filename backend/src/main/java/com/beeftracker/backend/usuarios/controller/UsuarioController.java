package com.beeftracker.backend.usuarios.controller;

import com.beeftracker.backend.auth.models.user.UserData;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.usuarios.services.UsuarioService;
import com.resend.core.exception.ResendException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody UserData user) throws ResendException {
        service.cadastrar(user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> finalizarCadastro(@PathVariable Long id, @RequestBody NovaSenha senha, @RequestParam String token) throws ResourceNotFoundException {
        service.finalizarCadastro(id, senha.senha(), token);
        return ResponseEntity.ok().build();
    }
}

record NovaSenha(
        String senha
){}
