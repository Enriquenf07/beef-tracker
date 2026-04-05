package com.beeftracker.backend.usuarios.controller;

import com.beeftracker.backend.auth.models.user.UserData;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.usuarios.models.RolesFull;
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

    @GetMapping
    public ResponseEntity<?> fetch()  {
        return ResponseEntity.ok(service.pesquisar(null, null));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody UserData user) throws ResendException {
        service.cadastrar(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody UserData user) throws ResourceNotFoundException {
        //roles id e criar método
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> Inativar(@PathVariable Long id) throws ResourceNotFoundException  {
        service.editarStatus(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarCadastro(@PathVariable Long id, @RequestBody NovaSenha senha, @RequestParam String token) throws ResourceNotFoundException {
        service.finalizarCadastro(id, senha.senha(), token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<?> getRoles(@PathVariable Long id)  {
        return ResponseEntity.ok(service.getRoles(id));
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles()  {
        RolesFull roles =  service.getAllRoles();
        return ResponseEntity.ok(roles);
    }

}


record NovaSenha(
        String senha
){}
