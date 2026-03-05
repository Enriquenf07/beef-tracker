package com.beeftracker.backend.auth.repositories;

import com.beeftracker.backend.auth.models.user.UserData;

public interface UserCustomRepository {
    void salvar(UserData userData, String token);
    void finalizarCadastro(Long id, String senha);
}
