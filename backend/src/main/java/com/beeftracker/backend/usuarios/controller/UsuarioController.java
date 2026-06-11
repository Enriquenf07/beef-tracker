package com.beeftracker.backend.usuarios.controller;

import com.beeftracker.backend.auth.models.user.UserData;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.base.exceptions.UnauthorizedException;
import com.beeftracker.backend.usuarios.models.RolesFull;
import com.beeftracker.backend.usuarios.services.UsuarioService;
import com.beeftracker.backend.usuarios.services.ValidateRoleService;
import com.resend.core.exception.ResendException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService service;
    private final ValidateRoleService roleService;

    public UsuarioController(UsuarioService service, ValidateRoleService roleService) {
        this.service = service;
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<?> fetch(
            @RequestAttribute("userId") String userId,
            @RequestParam(required = false) String chave,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) Integer page)
            throws UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN"));
        return ResponseEntity.ok(service.pesquisar(chave, status, page));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestAttribute("userId") String userId,
            @RequestBody UserData user)
            throws ResendException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN"));
        service.cadastrar(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reenviar-email")
    public ResponseEntity<?> reenviarEmail(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id)
            throws ResendException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN"));
        service.reenviarEmail(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id,
            @RequestBody UserData user)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN"));
        service.editar(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN"));
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> Inativar(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN"));
        service.editarStatus(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/finalizar")
    public ResponseEntity<?> finalizarCadastro(@RequestBody NovaSenha body)
            throws ResourceNotFoundException {
        service.finalizarCadastro(body.senha(), body.token());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<?> getRoles(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRoles(id));
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(service.getAllRoles());
    }

    @GetMapping("/motoristas")
    public ResponseEntity<?> getAllMotoristas(
            @RequestAttribute("userId") String userId)
            throws UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN"));
        return ResponseEntity.ok(service.listAllMotoristas());
    }
}

record NovaSenha(
        String senha,
        String token) {
}