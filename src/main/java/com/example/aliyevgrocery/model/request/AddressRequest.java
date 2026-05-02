package com.example.aliyevgrocery.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank
    private String city;

    @NotBlank
    private String street;

    private String building;

    private String apartment;

    private String note;
}
