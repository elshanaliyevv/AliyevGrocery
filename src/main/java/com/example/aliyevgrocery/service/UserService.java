package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.model.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUsername(String username);

    UserResponse updateEmail(String email);

    UserResponse updateNumber(String number);

    void deleteUser(Long id);
}
