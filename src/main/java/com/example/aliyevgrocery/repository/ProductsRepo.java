package com.example.aliyevgrocery.repository;

import com.example.aliyevgrocery.model.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductsRepo extends JpaRepository<Products, Long> {

    Optional<Products> findByName(String name);

    boolean existsByName(String name);

    List<Products> findAllByCategoriesId(Long categoriesId);
}
