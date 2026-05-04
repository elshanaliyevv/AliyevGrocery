package com.example.aliyevgrocery.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UserProductsSummaryResponse {
    private List<UserProductsResponse> items;
    private BigDecimal totalPrice;
}
