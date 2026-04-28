package com.example.aliyevgrocery.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressResponse {
    private Long id;
    private Long userId;
    private String city;
    private String street;
    private String building;
    private String apartment;
    private String note;
}
