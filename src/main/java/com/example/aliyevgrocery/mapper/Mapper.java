package com.example.aliyevgrocery.mapper;

import com.example.aliyevgrocery.Enums.OrderStatus;
import com.example.aliyevgrocery.Enums.Roles;
import com.example.aliyevgrocery.model.entity.Address;
import com.example.aliyevgrocery.model.entity.Categories;
import com.example.aliyevgrocery.model.entity.Products;
import com.example.aliyevgrocery.model.entity.User;
import com.example.aliyevgrocery.model.entity.UserProducts;
import com.example.aliyevgrocery.model.request.AddressRequest;
import com.example.aliyevgrocery.model.request.CategoryRequest;
import com.example.aliyevgrocery.model.request.ProductRequest;
import com.example.aliyevgrocery.model.request.UserProductsRequest;
import com.example.aliyevgrocery.model.request.UserRegister;
import com.example.aliyevgrocery.model.response.AddressResponse;
import com.example.aliyevgrocery.model.response.AuthResponse;
import com.example.aliyevgrocery.model.response.CategoryResponse;
import com.example.aliyevgrocery.model.response.ProductResponse;
import com.example.aliyevgrocery.model.response.TokensResponse;
import com.example.aliyevgrocery.model.response.UserProductsResponse;
import com.example.aliyevgrocery.model.response.UserResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class Mapper {

    public User toUser(UserRegister request, String encodedPassword) {
        return User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .number(request.getNumber())
                .role(Roles.USER)
                .isActive(true)
                .build();
    }

    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNumber(user.getNumber());
        response.setRole(user.getRole());
        return response;
    }

    public AuthResponse toAuthResponse(User user, TokensResponse tokensResponse) {
        AuthResponse response = new AuthResponse();
        response.setUserResponse(toUserResponse(user));
        response.setTokensResponse(tokensResponse);
        return response;
    }

    public Address toAddress(AddressRequest request, User user) {
        Address address = new Address();
        address.setUser(user);
        address.setCity(request.getCity());
        address.setStreet(request.getStreet());
        address.setBuilding(request.getBuilding());
        address.setApartment(request.getApartment());
        address.setNote(request.getNote());
        return address;
    }

    public AddressResponse toAddressResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setUserId(address.getUser().getId());
        response.setCity(address.getCity());
        response.setStreet(address.getStreet());
        response.setBuilding(address.getBuilding());
        response.setApartment(address.getApartment());
        response.setNote(address.getNote());
        return response;
    }

    public Categories toCategory(CategoryRequest request) {
        Categories category = new Categories();
        category.setName(request.getName());
        return category;
    }

    public CategoryResponse toCategoryResponse(Categories category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        return response;
    }

    public Products toProduct(ProductRequest request, Categories category) {
        Products product = new Products();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setCategories(category);
        product.setIsActive(true);
        return product;
    }

    public ProductResponse toProductResponse(Products product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setImageUrl(product.getImageUrl());
        response.setCategoryId(product.getCategories() != null ? product.getCategories().getId() : null);
        response.setCategoryName(product.getCategories() != null ? product.getCategories().getName() : null);
        response.setIsActive(product.getIsActive());
        response.setCreatedAt(product.getCreated_at());
        response.setUpdatedAt(product.getUpdated_at());
        return response;
    }

    public UserProducts toUserProducts(UserProductsRequest request, User user, Products product) {
        UserProducts userProducts = new UserProducts();
        userProducts.setUser(user);
        userProducts.setProduct(product);
        userProducts.setQuantity(request.getQuantity());
        userProducts.setUnitPrice(product.getPrice());
        userProducts.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        userProducts.setStatus(OrderStatus.CART);
        return userProducts;
    }

    public UserProductsResponse toUserProductsResponse(UserProducts userProducts) {
        BigDecimal unitPrice = userProducts.getUnitPrice() != null
                ? userProducts.getUnitPrice()
                : userProducts.getProduct().getPrice();
        BigDecimal totalPrice = userProducts.getTotalPrice() != null
                ? userProducts.getTotalPrice()
                : unitPrice.multiply(BigDecimal.valueOf(userProducts.getQuantity()));

        UserProductsResponse response = new UserProductsResponse();
        response.setId(userProducts.getId());
        response.setUserId(userProducts.getUser().getId());
        response.setProductId(userProducts.getProduct().getId());
        response.setProductName(userProducts.getProduct().getName());
        response.setQuantity(userProducts.getQuantity());
        response.setUnitPrice(unitPrice);
        response.setTotalPrice(totalPrice);
        response.setStatus(userProducts.getStatus());
        response.setCreatedAt(userProducts.getCreatedAt());
        response.setUpdatedAt(userProducts.getUpdatedAt());
        return response;
    }
}
