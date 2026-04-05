package com.beeftracker.backend.auth.repositories;

import com.beeftracker.backend.auth.models.user.User;
import com.beeftracker.backend.auth.models.user.UserData;
import com.beeftracker.backend.usuarios.models.Roles;
import com.beeftracker.backend.usuarios.models.RolesFull;

import java.util.List;

public interface UserCustomRepository {
    void salvar(UserData userData, String token, String senhaTemp);
    void finalizarCadastro(Long id, String senha);

    RolesFull findRolesByUser(Long userId);
    RolesFull findAllRoles();

    void editarStatus(Long id, boolean b);
    List<User> pesquisar(String chave, Boolean status);
    User carregar(Long id);
}
