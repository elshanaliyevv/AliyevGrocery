package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.Enums.OrderStatus;
import com.example.aliyevgrocery.Enums.Roles;
import com.example.aliyevgrocery.exception.InvalidOrderStatusException;
import com.example.aliyevgrocery.mapper.Mapper;
import com.example.aliyevgrocery.model.entity.Products;
import com.example.aliyevgrocery.model.entity.User;
import com.example.aliyevgrocery.model.entity.UserProducts;
import com.example.aliyevgrocery.model.request.UpdateUserProductQuantityRequest;
import com.example.aliyevgrocery.model.request.UserProductsRequest;
import com.example.aliyevgrocery.model.response.UserProductsResponse;
import com.example.aliyevgrocery.model.response.UserProductsSummaryResponse;
import com.example.aliyevgrocery.repository.ProductsRepo;
import com.example.aliyevgrocery.repository.UserProductsRepo;
import com.example.aliyevgrocery.repository.UserRepo;
import com.example.aliyevgrocery.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProductsServiceImplTest {

    @Mock
    private UserProductsRepo userProductsRepo;

    @Mock
    private ProductsRepo productsRepo;

    @Mock
    private UserRepo userRepo;

    private User user;
    private Products product;
    private UserProductsServiceImpl service;

    @BeforeEach
    void setUp() {
        user = user(1L);
        product = product(10L, "Apple", BigDecimal.valueOf(12.50));
        service = new UserProductsServiceImpl(userProductsRepo, productsRepo, userRepo, new Mapper());

        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(new CustomUserDetails(user), null, List.of())
        );

        lenient().when(userRepo.findById(1L)).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addProductCreatesCartItemWithCalculatedPrice() {
        when(productsRepo.findByIdAndIsActiveTrue(10L)).thenReturn(Optional.of(product));
        when(userProductsRepo.findByUserIdAndProductIdAndStatus(1L, 10L, OrderStatus.CART))
                .thenReturn(Optional.empty());
        when(userProductsRepo.save(any(UserProducts.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProductsResponse response = service.addProduct(userProductsRequest(10L, 2));

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getUnitPrice()).isEqualByComparingTo("12.5");
        assertThat(response.getTotalPrice()).isEqualByComparingTo("25.0");
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CART);
    }

    @Test
    void addProductMergesExistingCartItem() {
        UserProducts existing = userProduct(5L, user, product, 1, OrderStatus.CART);

        when(productsRepo.findByIdAndIsActiveTrue(10L)).thenReturn(Optional.of(product));
        when(userProductsRepo.findByUserIdAndProductIdAndStatus(1L, 10L, OrderStatus.CART))
                .thenReturn(Optional.of(existing));
        when(userProductsRepo.save(any(UserProducts.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProductsResponse response = service.addProduct(userProductsRequest(10L, 2));

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getQuantity()).isEqualTo(3);
        assertThat(response.getTotalPrice()).isEqualByComparingTo("37.5");
    }

    @Test
    void updateCartItemQuantityRecalculatesTotal() {
        UserProducts cartItem = userProduct(5L, user, product, 2, OrderStatus.CART);

        when(userProductsRepo.findById(5L)).thenReturn(Optional.of(cartItem));
        when(productsRepo.findByIdAndIsActiveTrue(10L)).thenReturn(Optional.of(product));
        when(userProductsRepo.save(any(UserProducts.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateUserProductQuantityRequest request = new UpdateUserProductQuantityRequest();
        request.setQuantity(4);

        UserProductsResponse response = service.updateCartItemQuantity(5L, request);

        assertThat(response.getQuantity()).isEqualTo(4);
        assertThat(response.getTotalPrice()).isEqualByComparingTo("50.0");
    }

    @Test
    void updateCartItemQuantityRejectsAnotherUsersItem() {
        User anotherUser = user(2L);
        UserProducts cartItem = userProduct(5L, anotherUser, product, 2, OrderStatus.CART);

        when(userProductsRepo.findById(5L)).thenReturn(Optional.of(cartItem));

        UpdateUserProductQuantityRequest request = new UpdateUserProductQuantityRequest();
        request.setQuantity(4);

        assertThatThrownBy(() -> service.updateCartItemQuantity(5L, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void placeOrderMovesCartItemsToPendingAndReturnsTotal() {
        UserProducts first = userProduct(5L, user, product, 2, OrderStatus.CART);
        UserProducts second = userProduct(6L, user, product, 1, OrderStatus.CART);

        when(userProductsRepo.findAllByUserIdAndStatusOrderByCreatedAtDesc(1L, OrderStatus.CART))
                .thenReturn(List.of(first, second));
        when(userProductsRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        UserProductsSummaryResponse response = service.placeOrder();

        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems()).extracting(UserProductsResponse::getStatus)
                .containsOnly(OrderStatus.PENDING);
        assertThat(response.getTotalPrice()).isEqualByComparingTo("37.5");
    }

    @Test
    void placeOrderRejectsEmptyCart() {
        when(userProductsRepo.findAllByUserIdAndStatusOrderByCreatedAtDesc(1L, OrderStatus.CART))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.placeOrder())
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessage("Sifariş vermək üçün səbətdə məhsul yoxdur");
    }

    @Test
    void cancelMyOrderOnlyCancelsPendingOrder() {
        UserProducts pendingOrder = userProduct(5L, user, product, 2, OrderStatus.PENDING);

        when(userProductsRepo.findById(5L)).thenReturn(Optional.of(pendingOrder));
        when(userProductsRepo.save(any(UserProducts.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProductsResponse response = service.cancelMyOrder(5L);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelMyOrderRejectsNonPendingOrder() {
        UserProducts preparingOrder = userProduct(5L, user, product, 2, OrderStatus.PREPARING);

        when(userProductsRepo.findById(5L)).thenReturn(Optional.of(preparingOrder));

        assertThatThrownBy(() -> service.cancelMyOrder(5L))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessage("Yalnız gözləmədə olan sifarişi ləğv edə bilərsiniz");
    }

    @Test
    void updateStatusUpdatesCheckedOutOrder() {
        UserProducts pendingOrder = userProduct(5L, user, product, 2, OrderStatus.PENDING);

        when(userProductsRepo.findById(5L)).thenReturn(Optional.of(pendingOrder));
        when(userProductsRepo.save(any(UserProducts.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProductsResponse response = service.updateStatus(5L, OrderStatus.PREPARING);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.PREPARING);
    }

    @Test
    void updateStatusRejectsCartItemAndCartTarget() {
        UserProducts cartItem = userProduct(5L, user, product, 2, OrderStatus.CART);
        UserProducts pendingOrder = userProduct(6L, user, product, 2, OrderStatus.PENDING);

        when(userProductsRepo.findById(5L)).thenReturn(Optional.of(cartItem));
        when(userProductsRepo.findById(6L)).thenReturn(Optional.of(pendingOrder));

        assertThatThrownBy(() -> service.updateStatus(5L, OrderStatus.PREPARING))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessage("Səbətdə olan məhsulun statusu dəyişdirilə bilməz");

        assertThatThrownBy(() -> service.updateStatus(6L, OrderStatus.CART))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessage("Sifariş statusu CART ola bilməz");
    }

    private UserProductsRequest userProductsRequest(Long productId, Integer quantity) {
        UserProductsRequest request = new UserProductsRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        return request;
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("user" + id);
        user.setPassword("Passw0rd1");
        user.setEmail("user" + id + "@example.com");
        user.setNumber("+994501234567");
        user.setRole(Roles.USER);
        user.setIsActive(true);
        return user;
    }

    private Products product(Long id, String name, BigDecimal price) {
        Products product = new Products();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setIsActive(true);
        return product;
    }

    private UserProducts userProduct(Long id, User user, Products product, Integer quantity, OrderStatus status) {
        UserProducts userProducts = new UserProducts();
        userProducts.setId(id);
        userProducts.setUser(user);
        userProducts.setProduct(product);
        userProducts.setQuantity(quantity);
        userProducts.setUnitPrice(product.getPrice());
        userProducts.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        userProducts.setStatus(status);
        return userProducts;
    }
}
