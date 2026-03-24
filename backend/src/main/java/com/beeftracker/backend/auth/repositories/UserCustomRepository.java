package com.beeftracker.backend.auth.repositories;

import com.beeftracker.backend.auth.models.user.User;
import com.beeftracker.backend.auth.models.user.UserData;
import com.beeftracker.backend.usuarios.models.Roles;

import java.util.List;

public interface UserCustomRepository {
    void salvar(UserData userData, String token, String senhaTemp);
    void finalizarCadastro(Long id, String senha);

    Roles findRolesByUser(Long userId);

    List<User> pesquisar(String chave, Boolean status);
}
