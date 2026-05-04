package com.example.aliyevgrocery.model.request;

import com.example.aliyevgrocery.Enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull
    private OrderStatus status;
}
