package com.example.aliyevgrocery.model.response;

import com.example.aliyevgrocery.Enums.Roles;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String number;
    private Roles role;
}