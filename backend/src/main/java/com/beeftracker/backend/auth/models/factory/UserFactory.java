package com.beeftracker.backend.auth.models.factory;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.auth.models.user.User;
import com.beeftracker.backend.auth.models.user.UserData;
import com.beeftracker.backend.base.query.MetaDataBuilder;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class UserFactory {

    public User create(ResultSet rs) throws SQLException {
        Array sqlArray = rs.getArray("roles");
        Array sqlArrayId = rs.getArray("rolesId");
        List<String> roles;
        List<Long> rolesId;

        if (sqlArray != null) {
            Object[] objArray = (Object[]) sqlArray.getArray();
            roles = Arrays.stream(objArray)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toList();
        } else {
            roles = List.of();
        }

        if (sqlArrayId != null) {
            Object[] objArray = (Object[]) sqlArrayId.getArray();
            rolesId = Arrays.stream(objArray)
                    .filter(Objects::nonNull)
                    .map(obj -> Long.parseLong(obj.toString()))
                    .toList();
        } else {
            rolesId = List.of();
        }
        UserData data = new UserDataBuilder()
                .roles(roles)
                .rolesId(rolesId)
                .nome(rs.getString("nome"))
                .ativo(rs.getBoolean("ativo"))
                .email(rs.getString("email"))
                .cadastrado(rs.getBoolean("cadastrado"))
                .build();
        Metadata metadata = new MetaDataBuilder()
                .id(rs.getLong("id"))
                .token(rs.getString("token"))
                .build();
        return new User(
                data,
                metadata);
    }
}
