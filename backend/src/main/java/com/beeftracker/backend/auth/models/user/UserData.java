package com.beeftracker.backend.auth.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;

import java.util.List;

public record UserData(
        String nome,
        Boolean ativo,
        @Transient
        List<String> roles,
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String senha,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String tokenCriadoEm,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String tokenPrimeiroAcesso

        ) {
    @PersistenceCreator
    public UserData(Boolean ativo, String email, String senha, String nome, String tokenCriadoEm, String tokenPrimeiroAcesso) {
        this(nome, ativo, List.of(), email, senha, tokenCriadoEm, tokenPrimeiroAcesso);
    }
}
