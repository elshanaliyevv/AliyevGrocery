package com.example.aliyevgrocery.model.request;

import com.example.aliyevgrocery.Enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserProductsRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Integer quantity;

    private OrderStatus status;
}
