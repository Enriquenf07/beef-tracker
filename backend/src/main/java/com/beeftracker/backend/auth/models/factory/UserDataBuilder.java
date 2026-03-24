package com.beeftracker.backend.auth.models.factory;

import com.beeftracker.backend.auth.models.user.UserData;

import java.util.List;

public class UserDataBuilder {
    private String nome;
    private Boolean ativo;
    private List<String> roles = List.of();
    private String email;
    private String senha;
    private String tokenCriadoEm;
    private String tokenPrimeiroAcesso;

    public UserDataBuilder nome(String nome) {
        this.nome = nome;
        return this;
    }

    public UserDataBuilder ativo(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public UserDataBuilder roles(List<String> roles) {
        this.roles = roles;
        return this;
    }

    public UserDataBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserDataBuilder senha(String senha) {
        this.senha = senha;
        return this;
    }

    public UserDataBuilder tokenCriadoEm(String token) {
        this.tokenCriadoEm = token;
        return this;
    }

    public UserDataBuilder tokenPrimeiroAcesso(String token) {
        this.tokenPrimeiroAcesso = token;
        return this;
    }

    public UserData build() {
        return new UserData(nome, ativo, roles, email, senha, tokenCriadoEm, tokenPrimeiroAcesso);
    }
}
