package com.example.aliyevgrocery.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String city;

    @NotBlank
    private String street;

    private String building;

    private String apartment;

    private String note;
}
