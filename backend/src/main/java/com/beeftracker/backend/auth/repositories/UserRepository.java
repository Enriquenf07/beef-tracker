package com.beeftracker.backend.auth.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.auth.models.user.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{
    public User findByDataEmail(String email);
}
