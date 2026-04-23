package com.example.aliyevgrocery.model.response;

import lombok.Data;

@Data
public class TokensResponse {
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpries;
    private long refreshTokenExpires;
}