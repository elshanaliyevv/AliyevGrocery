package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.model.request.ProductRequest;
import com.example.aliyevgrocery.model.response.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    List<ProductResponse> getProductsByCategoryId(Long categoryId);

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
