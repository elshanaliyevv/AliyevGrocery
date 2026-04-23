package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.model.request.UserLogin;
import com.example.aliyevgrocery.model.request.UserRegister;
import com.example.aliyevgrocery.model.response.AuthResponse;

public interface AuthService {
    AuthResponse register(UserRegister userRegister);
    AuthResponse login(UserLogin userLogin);
}