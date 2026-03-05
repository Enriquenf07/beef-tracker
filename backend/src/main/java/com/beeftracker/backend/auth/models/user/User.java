package com.beeftracker.backend.auth.models.user;

import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import com.beeftracker.backend.auth.models.metadata.Metadata;

@Table("usuarios")
public record User(
                @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) UserData data,
                @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Metadata metadata) {
}
