package com.example.aliyevgrocery.repository;

import com.example.aliyevgrocery.Enums.OrderStatus;
import com.example.aliyevgrocery.model.entity.UserProducts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProductsRepo extends JpaRepository<UserProducts, Long> {

    List<UserProducts> findAllByUserId(Long userId);

    List<UserProducts> findAllByProductId(Long productId);

    List<UserProducts> findAllByStatus(OrderStatus status);
}
