package com.example.aliyevgrocery.security;

import com.example.aliyevgrocery.model.entity.User;
import com.example.aliyevgrocery.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepo
                .findByUsernameOrEmailOrNumber(identifier, identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User tapılmadı: " + identifier));

        return new CustomUserDetails(user);
    }
}