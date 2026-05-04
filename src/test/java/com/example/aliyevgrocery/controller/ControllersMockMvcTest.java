package com.example.aliyevgrocery.controller;

import com.example.aliyevgrocery.Enums.OrderStatus;
import com.example.aliyevgrocery.exception.GlobalExceptionHandler;
import com.example.aliyevgrocery.model.request.UpdateOrderStatusRequest;
import com.example.aliyevgrocery.model.response.AddressResponse;
import com.example.aliyevgrocery.model.response.AuthResponse;
import com.example.aliyevgrocery.model.response.TokensResponse;
import com.example.aliyevgrocery.model.response.UserProductsResponse;
import com.example.aliyevgrocery.model.response.UserProductsSummaryResponse;
import com.example.aliyevgrocery.model.response.UserResponse;
import com.example.aliyevgrocery.service.AddressService;
import com.example.aliyevgrocery.service.AuthService;
import com.example.aliyevgrocery.service.JwtService;
import com.example.aliyevgrocery.service.UserProductsService;
import com.example.aliyevgrocery.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ControllersMockMvcTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private AddressService addressService;

    @Mock
    private UserProductsService userProductsService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new AuthController(authService, jwtService),
                        new UserController(userService),
                        new AddressController(addressService),
                        new UserProductsController(userProductsService)
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void authEndpointsDelegateToServices() throws Exception {
        when(authService.register(any())).thenReturn(authResponse(1L));
        when(authService.login(any())).thenReturn(authResponse(1L));
        when(jwtService.refreshAccessToken("refresh-token")).thenReturn(tokensResponse());

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "user123",
                                  "password": "Passw0rd1",
                                  "email": "user@example.com",
                                  "number": "+994501234567"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userResponse.id").value(1));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "identifier": "user123",
                                  "password": "Passw0rd1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokensResponse.accessToken").value("access-token"));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content("""
                                {
                                  "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));

        verify(authService).register(any());
        verify(authService).login(any());
        verify(jwtService).refreshAccessToken("refresh-token");
    }

    @Test
    void userEndpointsDelegateToService() throws Exception {
        UserResponse user = userResponse(1L);
        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.updateUsername("newuser")).thenReturn(user);
        when(userService.updateEmail("new@example.com")).thenReturn(user);
        when(userService.updateNumber("+994991234567")).thenReturn(user);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"));

        mockMvc.perform(patch("/api/users/me/username")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "newuser"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/users/me/email")
                        .contentType("application/json")
                        .content("""
                                {
                                  "email": "new@example.com"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/users/me/number")
                        .contentType("application/json")
                        .content("""
                                {
                                  "number": "+994991234567"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(2L);
    }

    @Test
    void addressEndpointsDelegateToService() throws Exception {
        AddressResponse address = addressResponse();
        when(addressService.getMyAddress()).thenReturn(address);
        when(addressService.createAddress(any())).thenReturn(address);
        when(addressService.updateMyAddress(any())).thenReturn(address);

        String body = """
                {
                  "city": "Baku",
                  "street": "Nizami",
                  "building": "10",
                  "apartment": "5",
                  "note": "Call before delivery"
                }
                """;

        mockMvc.perform(post("/api/addresses")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Baku"));

        mockMvc.perform(get("/api/addresses/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(put("/api/addresses/me")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("Nizami"));

        mockMvc.perform(delete("/api/addresses/1"))
                .andExpect(status().isNoContent());

        verify(addressService).deleteAddress(1L);
    }

    @Test
    void userProductsEndpointsDelegateToService() throws Exception {
        UserProductsResponse item = userProductsResponse(7L, OrderStatus.CART, BigDecimal.valueOf(25));
        UserProductsSummaryResponse summary = summary(item);

        when(userProductsService.getMyProducts()).thenReturn(List.of(item));
        when(userProductsService.getMyCart()).thenReturn(summary);
        when(userProductsService.addProduct(any())).thenReturn(item);
        when(userProductsService.updateCartItemQuantity(eq(7L), any())).thenReturn(item);
        when(userProductsService.placeOrder()).thenReturn(summary);
        when(userProductsService.cancelMyOrder(7L)).thenReturn(item);
        when(userProductsService.getAllProducts()).thenReturn(List.of(item));
        when(userProductsService.getProductsByStatus(OrderStatus.CART)).thenReturn(List.of(item));
        when(userProductsService.updateStatus(7L, OrderStatus.PREPARING))
                .thenReturn(userProductsResponse(7L, OrderStatus.PREPARING, BigDecimal.valueOf(25)));

        mockMvc.perform(get("/api/user-products/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7));

        mockMvc.perform(get("/api/user-products/me/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(25));

        mockMvc.perform(post("/api/user-products")
                        .contentType("application/json")
                        .content("""
                                {
                                  "productId": 10,
                                  "quantity": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CART"));

        mockMvc.perform(patch("/api/user-products/7/quantity")
                        .contentType("application/json")
                        .content("""
                                {
                                  "quantity": 3
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/user-products/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(7));

        mockMvc.perform(patch("/api/user-products/7/cancel"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(10));

        mockMvc.perform(get("/api/user-products/status/CART"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CART"));

        mockMvc.perform(patch("/api/user-products/7/status")
                        .contentType("application/json")
                        .content("""
                                {
                                  "status": "PREPARING"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }

    @Test
    void validationErrorsReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/user-products")
                        .contentType("application/json")
                        .content("""
                                {
                                  "productId": 10,
                                  "quantity": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation xətası"));
    }

    @Test
    void adminAndCourierEndpointsKeepPreAuthorizeRules() throws Exception {
        assertPreAuthorize(
                UserController.class.getMethod("getAllUsers"),
                "hasRole('ADMIN')"
        );
        assertPreAuthorize(
                AddressController.class.getMethod("deleteAddress", Long.class),
                "hasRole('ADMIN')"
        );
        assertPreAuthorize(
                UserProductsController.class.getMethod("updateStatus", Long.class, UpdateOrderStatusRequest.class),
                "hasAnyRole('ADMIN', 'COURIER')"
        );
    }

    private void assertPreAuthorize(Method method, String expected) {
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo(expected);
    }

    private AuthResponse authResponse(Long userId) {
        AuthResponse response = new AuthResponse();
        response.setUserResponse(userResponse(userId));
        response.setTokensResponse(tokensResponse());
        return response;
    }

    private TokensResponse tokensResponse() {
        TokensResponse response = new TokensResponse();
        response.setAccessToken("access-token");
        response.setRefreshToken("refresh-token");
        response.setAccessTokenExpries(300000);
        response.setRefreshTokenExpires(604800000);
        return response;
    }

    private UserResponse userResponse(Long id) {
        UserResponse response = new UserResponse();
        response.setId(id);
        response.setUsername("user" + id);
        response.setEmail("user" + id + "@example.com");
        response.setNumber("+994501234567");
        return response;
    }

    private AddressResponse addressResponse() {
        AddressResponse response = new AddressResponse();
        response.setId(1L);
        response.setUserId(1L);
        response.setCity("Baku");
        response.setStreet("Nizami");
        response.setBuilding("10");
        response.setApartment("5");
        response.setNote("Call before delivery");
        return response;
    }

    private UserProductsResponse userProductsResponse(Long id, OrderStatus status, BigDecimal totalPrice) {
        UserProductsResponse response = new UserProductsResponse();
        response.setId(id);
        response.setUserId(1L);
        response.setProductId(10L);
        response.setProductName("Apple");
        response.setQuantity(2);
        response.setUnitPrice(BigDecimal.valueOf(12.5));
        response.setTotalPrice(totalPrice);
        response.setStatus(status);
        return response;
    }

    private UserProductsSummaryResponse summary(UserProductsResponse item) {
        UserProductsSummaryResponse response = new UserProductsSummaryResponse();
        response.setItems(List.of(item));
        response.setTotalPrice(item.getTotalPrice());
        return response;
    }
}
