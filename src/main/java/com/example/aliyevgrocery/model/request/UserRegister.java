package com.example.aliyevgrocery.model.request;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data

public class UserRegister {

    @NotBlank

    @Size(min = 3, max = 15)

    private String username;

    @Size(min = 8)

    private String password;
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;
    @Pattern(regexp = "^(?:\\+994|0)?(?:50|51|55|70|77|99|10)\\d{7}$")
    private String number;

}