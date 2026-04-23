package com.example.aliyevgrocery.repository;

import com.example.aliyevgrocery.model.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByNumber(String number);

    Optional<User> findByUsernameOrEmailOrNumber(String username, String email, String number);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNumber(String number);

}