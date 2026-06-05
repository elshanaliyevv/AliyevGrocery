package com.example.aliyevgrocery.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateUserProductQuantityRequest {
    @NotNull
    @PositiveOrZero
    private Integer quantity;
}
