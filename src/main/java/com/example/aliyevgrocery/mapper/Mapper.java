package com.example.aliyevgrocery.mapper;

import com.example.aliyevgrocery.Enums.OrderStatus;
import com.example.aliyevgrocery.Enums.Roles;
import com.example.aliyevgrocery.model.entity.Address;
import com.example.aliyevgrocery.model.entity.Products;
import com.example.aliyevgrocery.model.entity.User;
import com.example.aliyevgrocery.model.entity.UserProducts;
import com.example.aliyevgrocery.model.request.AddressRequest;
import com.example.aliyevgrocery.model.request.UserProductsRequest;
import com.example.aliyevgrocery.model.request.UserRegister;
import com.example.aliyevgrocery.model.response.AddressResponse;
import com.example.aliyevgrocery.model.response.AuthResponse;
import com.example.aliyevgrocery.model.response.TokensResponse;
import com.example.aliyevgrocery.model.response.UserProductsResponse;
import com.example.aliyevgrocery.model.response.UserResponse;
import org.springframework.stereotype.Component;

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

    public UserProducts toUserProducts(UserProductsRequest request, User user, Products product) {
        UserProducts userProducts = new UserProducts();
        userProducts.setUser(user);
        userProducts.setProduct(product);
        userProducts.setQuantity(request.getQuantity());
        userProducts.setStatus(request.getStatus() != null ? request.getStatus() : OrderStatus.PREPARING);
        return userProducts;
    }

    public UserProductsResponse toUserProductsResponse(UserProducts userProducts) {
        UserProductsResponse response = new UserProductsResponse();
        response.setId(userProducts.getId());
        response.setUserId(userProducts.getUser().getId());
        response.setProductId(userProducts.getProduct().getId());
        response.setQuantity(userProducts.getQuantity());
        response.setStatus(userProducts.getStatus());
        response.setCreatedAt(userProducts.getCreatedAt());
        response.setUpdatedAt(userProducts.getUpdatedAt());
        return response;
    }
}
