package com.example.aliyevgrocery.repository;

import com.example.aliyevgrocery.model.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriesRepo extends JpaRepository<Categories, Long> {

    Optional<Categories> findByName(String name);

    boolean existsByName(String name);
}
