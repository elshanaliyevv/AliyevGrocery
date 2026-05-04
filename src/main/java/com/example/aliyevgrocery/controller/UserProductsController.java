package com.example.aliyevgrocery.controller;

import com.example.aliyevgrocery.Enums.OrderStatus;
import com.example.aliyevgrocery.model.request.UpdateOrderStatusRequest;
import com.example.aliyevgrocery.model.request.UpdateUserProductQuantityRequest;
import com.example.aliyevgrocery.model.request.UserProductsRequest;
import com.example.aliyevgrocery.model.response.UserProductsResponse;
import com.example.aliyevgrocery.model.response.UserProductsSummaryResponse;
import com.example.aliyevgrocery.service.UserProductsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-products")
@RequiredArgsConstructor
public class UserProductsController {

    private final UserProductsService userProductsService;

    @GetMapping("/me")
    public ResponseEntity<List<UserProductsResponse>> getMyProducts() {
        return ResponseEntity.ok(userProductsService.getMyProducts());
    }

    @GetMapping("/me/cart")
    public ResponseEntity<UserProductsSummaryResponse> getMyCart() {
        return ResponseEntity.ok(userProductsService.getMyCart());
    }

    @PostMapping
    public ResponseEntity<UserProductsResponse> addProduct(@Valid @RequestBody UserProductsRequest request) {
        return ResponseEntity.ok(userProductsService.addProduct(request));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<UserProductsResponse> updateCartItemQuantity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserProductQuantityRequest request
    ) {
        return ResponseEntity.ok(userProductsService.updateCartItemQuantity(id, request));
    }

    @PostMapping("/order")
    public ResponseEntity<UserProductsSummaryResponse> placeOrder() {
        return ResponseEntity.ok(userProductsService.placeOrder());
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<UserProductsResponse> cancelMyOrder(@PathVariable Long id) {
        return ResponseEntity.ok(userProductsService.cancelMyOrder(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
    public ResponseEntity<List<UserProductsResponse>> getAllProducts() {
        return ResponseEntity.ok(userProductsService.getAllProducts());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
    public ResponseEntity<List<UserProductsResponse>> getProductsByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(userProductsService.getProductsByStatus(status));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
    public ResponseEntity<UserProductsResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(userProductsService.updateStatus(id, request.getStatus()));
    }
}
