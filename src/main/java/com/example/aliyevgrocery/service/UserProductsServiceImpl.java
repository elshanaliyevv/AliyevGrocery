package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.Enums.OrderStatus;
import com.example.aliyevgrocery.exception.InvalidOrderStatusException;
import com.example.aliyevgrocery.exception.ProductNotFoundException;
import com.example.aliyevgrocery.exception.UnauthorizedException;
import com.example.aliyevgrocery.exception.UserNotFoundException;
import com.example.aliyevgrocery.exception.UserProductNotFoundException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProductsServiceImpl implements UserProductsService {

    private final UserProductsRepo userProductsRepo;
    private final ProductsRepo productsRepo;
    private final UserRepo userRepo;
    private final Mapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserProductsResponse> getMyProducts() {
        User user = getAuthenticatedUser();

        return userProductsRepo.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(mapper::toUserProductsResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserProductsSummaryResponse getMyCart() {
        User user = getAuthenticatedUser();

        return toSummary(userProductsRepo.findAllByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), OrderStatus.CART));
    }

    @Override
    @Transactional
    public UserProductsResponse addProduct(UserProductsRequest request) {
        User user = getAuthenticatedUser();
        Products product = findActiveProductById(request.getProductId());

        UserProducts userProducts = userProductsRepo
                .findByUserIdAndProductIdAndStatus(user.getId(), product.getId(), OrderStatus.CART)
                .map(existingProduct -> {
                    existingProduct.setQuantity(existingProduct.getQuantity() + request.getQuantity());
                    existingProduct.setProduct(product);
                    refreshPrice(existingProduct, product);
                    return existingProduct;
                })
                .orElseGet(() -> mapper.toUserProducts(request, user, product));

        refreshPrice(userProducts, product);

        return mapper.toUserProductsResponse(userProductsRepo.save(userProducts));
    }

    @Override
    @Transactional
    public UserProductsResponse updateCartItemQuantity(Long id, UpdateUserProductQuantityRequest request) {
        User user = getAuthenticatedUser();
        UserProducts userProducts = findUserProductById(id);

        assertOwner(userProducts, user);
        assertStatus(userProducts, OrderStatus.CART, "Yalnız səbətdə olan məhsulun sayını dəyişə bilərsiniz");

        if (request.getQuantity() == 0) {
            userProductsRepo.delete(userProducts);
            return mapper.toUserProductsResponse(userProducts);
        }

        Products product = findActiveProductById(userProducts.getProduct().getId());
        userProducts.setQuantity(request.getQuantity());
        refreshPrice(userProducts, product);

        return mapper.toUserProductsResponse(userProductsRepo.save(userProducts));
    }

    @Override
    @Transactional
    public UserProductsSummaryResponse placeOrder() {
        User user = getAuthenticatedUser();
        List<UserProducts> cartProducts = userProductsRepo
                .findAllByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), OrderStatus.CART);

        if (cartProducts.isEmpty()) {
            throw new InvalidOrderStatusException("Sifariş vermək üçün səbətdə məhsul yoxdur");
        }

        cartProducts.forEach(userProduct -> userProduct.setStatus(OrderStatus.PENDING));

        return toSummary(userProductsRepo.saveAll(cartProducts));
    }

    @Override
    @Transactional
    public UserProductsResponse cancelMyOrder(Long id) {
        User user = getAuthenticatedUser();
        UserProducts userProducts = findUserProductById(id);

        assertOwner(userProducts, user);
        assertStatus(userProducts, OrderStatus.PENDING, "Yalnız gözləmədə olan sifarişi ləğv edə bilərsiniz");

        userProducts.setStatus(OrderStatus.CANCELLED);

        return mapper.toUserProductsResponse(userProductsRepo.save(userProducts));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProductsResponse> getAllProducts() {
        return userProductsRepo.findAll()
                .stream()
                .map(mapper::toUserProductsResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProductsResponse> getProductsByStatus(OrderStatus status) {
        return userProductsRepo.findAllByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(mapper::toUserProductsResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserProductsResponse updateStatus(Long id, OrderStatus status) {
        UserProducts userProducts = findUserProductById(id);

        if (userProducts.getStatus() == OrderStatus.CART) {
            throw new InvalidOrderStatusException("Səbətdə olan məhsulun statusu dəyişdirilə bilməz");
        }

        if (status == OrderStatus.CART) {
            throw new InvalidOrderStatusException("Sifariş statusu CART ola bilməz");
        }

        userProducts.setStatus(status);

        return mapper.toUserProductsResponse(userProductsRepo.save(userProducts));
    }

    private void refreshPrice(UserProducts userProducts, Products product) {
        BigDecimal unitPrice = product.getPrice();
        userProducts.setUnitPrice(unitPrice);
        userProducts.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(userProducts.getQuantity())));
    }

    private UserProductsSummaryResponse toSummary(List<UserProducts> userProducts) {
        List<UserProductsResponse> items = userProducts.stream()
                .map(mapper::toUserProductsResponse)
                .toList();

        BigDecimal totalPrice = items.stream()
                .map(UserProductsResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        UserProductsSummaryResponse response = new UserProductsSummaryResponse();
        response.setItems(items);
        response.setTotalPrice(totalPrice);
        return response;
    }

    private UserProducts findUserProductById(Long id) {
        return userProductsRepo.findById(id)
                .orElseThrow(() -> new UserProductNotFoundException("Sifariş məhsulu tapılmadı"));
    }

    private Products findActiveProductById(Long id) {
        return productsRepo.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException("Məhsul tapılmadı"));
    }

    private void assertOwner(UserProducts userProducts, User user) {
        if (!userProducts.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Bu sifariş sizə aid deyil");
        }
    }

    private void assertStatus(UserProducts userProducts, OrderStatus expectedStatus, String message) {
        if (userProducts.getStatus() != expectedStatus) {
            throw new InvalidOrderStatusException(message);
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new UnauthorizedException("İstifadəçi autentifikasiya olunmayıb");
        }

        return userRepo.findById(userDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));
    }
}
