package com.example.aliyevgrocery.repository;

import com.example.aliyevgrocery.Enums.OrderStatus;
import com.example.aliyevgrocery.model.entity.UserProducts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProductsRepo extends JpaRepository<UserProducts, Long> {

    List<UserProducts> findAllByUserId(Long userId);

    List<UserProducts> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<UserProducts> findAllByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status);

    List<UserProducts> findAllByProductId(Long productId);

    List<UserProducts> findAllByStatus(OrderStatus status);

    List<UserProducts> findAllByStatusOrderByCreatedAtDesc(OrderStatus status);

    Optional<UserProducts> findByUserIdAndProductIdAndStatus(Long userId, Long productId, OrderStatus status);
}
