package com.example.aliyevgrocery.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUsernameRequest {

    @NotBlank
    @Size(min = 3, max = 15)
    private String username;
}
