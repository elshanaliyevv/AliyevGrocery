package com.example.aliyevgrocery.controller;

import com.example.aliyevgrocery.model.request.UpdateEmailRequest;
import com.example.aliyevgrocery.model.request.UpdateNumberRequest;
import com.example.aliyevgrocery.model.request.UpdateUsernameRequest;
import com.example.aliyevgrocery.model.response.UserResponse;
import com.example.aliyevgrocery.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/me/username")
    public ResponseEntity<UserResponse> updateUsername(@Valid @RequestBody UpdateUsernameRequest request) {
        return ResponseEntity.ok(userService.updateUsername(request.getUsername()));
    }

    @PatchMapping("/me/email")
    public ResponseEntity<UserResponse> updateEmail(@Valid @RequestBody UpdateEmailRequest request) {
        return ResponseEntity.ok(userService.updateEmail(request.getEmail()));
    }

    @PatchMapping("/me/number")
    public ResponseEntity<UserResponse> updateNumber(@Valid @RequestBody UpdateNumberRequest request) {
        return ResponseEntity.ok(userService.updateNumber(request.getNumber()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
