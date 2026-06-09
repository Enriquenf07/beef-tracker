package com.beeftracker.backend.usuarios.services;

import com.beeftracker.backend.base.exceptions.UnauthorizedException;
import com.beeftracker.backend.usuarios.models.Role;
import com.beeftracker.backend.usuarios.models.RolesFull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidateRoleService {
    private final UsuarioService service;


    public ValidateRoleService(UsuarioService service) {
        this.service = service;
    }


    public void validate(Long id, List<String> roles) throws UnauthorizedException {
        List<Role> userRoles = service.getRoles(id).roles();

        List<String> userRoleNames = userRoles.stream()
                .map(Role::nome)
                .toList();

        boolean flag = roles.stream()
                .anyMatch(userRoleNames::contains);
        if(!flag){
            throw new UnauthorizedException();
        }
    }
}
