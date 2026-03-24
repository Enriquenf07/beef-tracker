package com.beeftracker.backend.base.query;

import com.beeftracker.backend.auth.models.metadata.Metadata;

import java.time.LocalDate;

public class MetaDataBuilder {
    private LocalDate criadoEm;
    private LocalDate atualizadoEm;
    private Long id;
    private String token;

    public MetaDataBuilder criadoEm(LocalDate criadoEm) {
        this.criadoEm = criadoEm;
        return this;
    }

    public MetaDataBuilder atualizadoEm(LocalDate atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
        return this;
    }

    public MetaDataBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public MetaDataBuilder token(String token) {
        this.token = token;
        return this;
    }

    public Metadata build() {
        return new Metadata(criadoEm, atualizadoEm, id, token);
    }
}
