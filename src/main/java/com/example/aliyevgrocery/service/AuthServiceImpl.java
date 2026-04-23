package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.Enums.Roles;
import com.example.aliyevgrocery.model.request.UserLogin;
import com.example.aliyevgrocery.model.request.UserRegister;
import com.example.aliyevgrocery.model.response.AuthResponse;
import com.example.aliyevgrocery.model.response.TokensResponse;
import com.example.aliyevgrocery.model.response.UserResponse;
import com.example.aliyevgrocery.model.entity.User;
import com.example.aliyevgrocery.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(UserRegister userRegister) {
        if (userRepo.existsByUsername(userRegister.getUsername())) {
            throw new RuntimeException("Bu username artıq mövcuddur");
        }

        if (StringUtils.hasText(userRegister.getEmail()) && userRepo.existsByEmail(userRegister.getEmail())) {
            throw new RuntimeException("Bu email artıq mövcuddur");
        }

        if (StringUtils.hasText(userRegister.getNumber()) && userRepo.existsByNumber(userRegister.getNumber())) {
            throw new RuntimeException("Bu nömrə artıq mövcuddur");
        }

        User user = User.builder()
                .username(userRegister.getUsername())
                .email(userRegister.getEmail())
                .number(userRegister.getNumber())
                .password(passwordEncoder.encode(userRegister.getPassword()))
                .role(Roles.USER)
                .isActive(true)
                .build();

        User savedUser = userRepo.save(user);
        TokensResponse tokens = jwtService.generateTokens(savedUser.getUsername());

        return buildAuthResponse(savedUser, tokens);
    }

    @Override
    public AuthResponse login(UserLogin userLogin) {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        userLogin.getIdentifier(),
                        userLogin.getPassword()
                )
        );

        User user = userRepo.findByUsernameOrEmailOrNumber(
                userLogin.getIdentifier(),
                userLogin.getIdentifier(),
                userLogin.getIdentifier()
        ).orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

        TokensResponse tokens = jwtService.generateTokens(user.getUsername());
        return buildAuthResponse(user, tokens);
    }

    private AuthResponse buildAuthResponse(User user, TokensResponse tokens) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setNumber(user.getNumber());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setTokensResponse(tokens);
        authResponse.setUserResponse(userResponse);
        return authResponse;
    }
}