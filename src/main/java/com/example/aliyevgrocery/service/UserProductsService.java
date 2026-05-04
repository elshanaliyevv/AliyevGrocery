package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.Enums.OrderStatus;
import com.example.aliyevgrocery.model.request.UpdateUserProductQuantityRequest;
import com.example.aliyevgrocery.model.request.UserProductsRequest;
import com.example.aliyevgrocery.model.response.UserProductsResponse;
import com.example.aliyevgrocery.model.response.UserProductsSummaryResponse;

import java.util.List;

public interface UserProductsService {

    List<UserProductsResponse> getMyProducts();

    UserProductsSummaryResponse getMyCart();

    UserProductsResponse addProduct(UserProductsRequest request);

    UserProductsResponse updateCartItemQuantity(Long id, UpdateUserProductQuantityRequest request);

    UserProductsSummaryResponse placeOrder();

    UserProductsResponse cancelMyOrder(Long id);

    List<UserProductsResponse> getAllProducts();

    List<UserProductsResponse> getProductsByStatus(OrderStatus status);

    UserProductsResponse updateStatus(Long id, OrderStatus status);
}
