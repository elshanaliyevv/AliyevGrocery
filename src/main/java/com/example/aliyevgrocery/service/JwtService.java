package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.model.response.TokensResponse;

public interface JwtService {
    TokensResponse generateTokens(String username);
    String extractUsernameFromAccessToken(String accessToken);
    String extractUsernameFromRefreshToken(String refreshToken);
    boolean isValidAccess(String token);
    boolean isValidRefresh(String token);
    TokensResponse refreshAccessToken(String refreshToken);
}