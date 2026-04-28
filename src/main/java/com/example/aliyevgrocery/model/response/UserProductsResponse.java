package com.example.aliyevgrocery.model.response;

import com.example.aliyevgrocery.Enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProductsResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
