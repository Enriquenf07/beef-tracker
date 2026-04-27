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
    public ResponseEntity<?> fetch(
            @RequestParam(required = false) String chave,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) Integer page

    ) {
        return ResponseEntity.ok(service.pesquisar(chave, status, page));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody UserData user) throws ResendException {
        service.cadastrar(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reenviar-email")
    public ResponseEntity<?> reenviarEmail(@PathVariable Long id) throws ResendException {
        service.reenviarEmail(id);
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

    @PatchMapping("/finalizar")
    public ResponseEntity<?> finalizarCadastro(@RequestBody NovaSenha body) throws ResourceNotFoundException {
        service.finalizarCadastro(body.senha(), body.token());
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
        String senha,
        String token
){}
