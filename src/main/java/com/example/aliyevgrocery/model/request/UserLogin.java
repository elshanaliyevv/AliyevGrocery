package com.example.aliyevgrocery.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLogin {
    @NotBlank
    private String identifier;

    @NotBlank
    private String password;
}