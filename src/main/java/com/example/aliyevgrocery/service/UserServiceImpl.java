package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.exception.EmailAlreadyExistsException;
import com.example.aliyevgrocery.mapper.Mapper;
import com.example.aliyevgrocery.exception.NumberAlreadyExistsException;
import com.example.aliyevgrocery.exception.UnauthorizedException;
import com.example.aliyevgrocery.exception.UserNotFoundException;
import com.example.aliyevgrocery.exception.UsernameAlreadyExistsException;
import com.example.aliyevgrocery.model.entity.User;
import com.example.aliyevgrocery.model.response.UserResponse;
import com.example.aliyevgrocery.repository.UserRepo;
import com.example.aliyevgrocery.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final Mapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(mapper::toUserResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return mapper.toUserResponse(findUserById(id));
    }

    @Override
    @Transactional
    public UserResponse updateUsername(String username) {
        User user = getAuthenticatedUser();

        if (!user.getUsername().equals(username) && userRepo.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Bu username artıq mövcuddur");
        }

        user.setUsername(username);
        return mapper.toUserResponse(userRepo.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateEmail(String email) {
        User user = getAuthenticatedUser();

        if (StringUtils.hasText(email)
                && !email.equals(user.getEmail())
                && userRepo.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Bu email artıq mövcuddur");
        }

        user.setEmail(email);
        return mapper.toUserResponse(userRepo.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateNumber(String number) {
        User user = getAuthenticatedUser();

        if (StringUtils.hasText(number)
                && !number.equals(user.getNumber())
                && userRepo.existsByNumber(number)) {
            throw new NumberAlreadyExistsException("Bu nömrə artıq mövcuddur");
        }

        user.setNumber(number);
        return mapper.toUserResponse(userRepo.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepo.delete(findUserById(id));
    }

    private User findUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new UnauthorizedException("İstifadəçi autentifikasiya olunmayıb");
        }

        return findUserById(userDetails.getId());
    }
}
