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
        List<String> roles;

        if (sqlArray != null) {
            Object[] objArray = (Object[]) sqlArray.getArray();
            roles = Arrays.stream(objArray)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toList();
        } else {
            roles = List.of();
        }

        UserData data = new UserDataBuilder()
                .roles(roles)
                .nome(rs.getString("nome"))
                .ativo(rs.getBoolean("ativo"))
                .email(rs.getString("email"))
                .build();
        Metadata metadata = new MetaDataBuilder()
                .id(rs.getLong("id"))
                .token(rs.getString("token"))
                .build();
        return new User(
                data,
                metadata
        );
    }
}
