package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.exception.CategoryNotFoundException;
import com.example.aliyevgrocery.exception.ProductAlreadyExistsException;
import com.example.aliyevgrocery.exception.ProductNotFoundException;
import com.example.aliyevgrocery.mapper.Mapper;
import com.example.aliyevgrocery.model.entity.Categories;
import com.example.aliyevgrocery.model.entity.Products;
import com.example.aliyevgrocery.model.request.ProductRequest;
import com.example.aliyevgrocery.model.response.ProductResponse;
import com.example.aliyevgrocery.repository.CategoriesRepo;
import com.example.aliyevgrocery.repository.ProductsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductsRepo productsRepo;
    private final CategoriesRepo categoriesRepo;
    private final Mapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productsRepo.findAllByIsActiveTrue()
                .stream()
                .map(mapper::toProductResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return mapper.toProductResponse(findProductById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        findCategoryById(categoryId);

        return productsRepo.findAllByCategoriesIdAndIsActiveTrue(categoryId)
                .stream()
                .map(mapper::toProductResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productsRepo.existsByNameAndIsActiveTrue(request.getName())) {
            throw new ProductAlreadyExistsException("Bu məhsul artıq mövcuddur");
        }

        Categories category = findCategoryById(request.getCategoryId());
        Products product = mapper.toProduct(request, category);

        return mapper.toProductResponse(productsRepo.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Products product = findProductById(id);

        if (!product.getName().equals(request.getName()) && productsRepo.existsByNameAndIsActiveTrue(request.getName())) {
            throw new ProductAlreadyExistsException("Bu məhsul artıq mövcuddur");
        }

        Categories category = findCategoryById(request.getCategoryId());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategories(category);

        return mapper.toProductResponse(productsRepo.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Products product = findProductById(id);
        product.setIsActive(false);
        productsRepo.save(product);
    }

    private Products findProductById(Long id) {
        return productsRepo.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException("Məhsul tapılmadı"));
    }

    private Categories findCategoryById(Long id) {
        return categoriesRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Kateqoriya tapılmadı"));
    }
}
