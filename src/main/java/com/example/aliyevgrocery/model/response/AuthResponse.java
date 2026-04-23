package com.example.aliyevgrocery.model.response;

import com.example.aliyevgrocery.model.response.TokensResponse;
import com.example.aliyevgrocery.model.response.UserResponse;
import lombok.Data;

@Data
public class AuthResponse {
    private TokensResponse tokensResponse;
    private UserResponse userResponse;
}