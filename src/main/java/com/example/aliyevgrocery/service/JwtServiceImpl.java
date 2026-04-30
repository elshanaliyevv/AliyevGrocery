package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.exception.InvalidRefreshTokenException;
import com.example.aliyevgrocery.model.response.TokensResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.access-expiration}")
    private long accessExpiration;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey signInKey;

    @PostConstruct
    void init() {
        this.signInKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    @Override
    public TokensResponse generateTokens(String username) {
        String accessToken = generateToken(username, accessExpiration, "access");
        String refreshToken = generateToken(username, refreshExpiration, "refresh");

        TokensResponse response = new TokensResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setAccessTokenExpries(accessExpiration);
        response.setRefreshTokenExpires(refreshExpiration);
        return response;
    }

    @Override
    public String extractUsernameFromAccessToken(String accessToken) {
        return extractClaim(accessToken, Claims::getSubject);
    }

    @Override
    public String extractUsernameFromRefreshToken(String refreshToken) {
        return extractClaim(refreshToken, Claims::getSubject);
    }

    @Override
    public boolean isValidAccess(String token) {
        return isValid(token, "access");
    }

    @Override
    public boolean isValidRefresh(String token) {
        return isValid(token, "refresh");
    }

    @Override
    public TokensResponse refreshAccessToken(String refreshToken) {
        if (!isValidRefresh(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh token etibarsızdır");
        }

        String username = extractUsernameFromRefreshToken(refreshToken);
        return generateTokens(username);
    }

    private String generateToken(String username, long expiration, String type) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .claim("type", type)
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(signInKey)
                .compact();
    }

    private boolean isValid(String token, String expectedType) {
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            return expectedType.equals(type) && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signInKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }
}
