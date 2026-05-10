package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.exception.CategoryAlreadyExistsException;
import com.example.aliyevgrocery.exception.CategoryNotFoundException;
import com.example.aliyevgrocery.mapper.Mapper;
import com.example.aliyevgrocery.model.entity.Categories;
import com.example.aliyevgrocery.model.request.CategoryRequest;
import com.example.aliyevgrocery.model.response.CategoryResponse;
import com.example.aliyevgrocery.repository.CategoriesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoriesRepo categoriesRepo;
    private final Mapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoriesRepo.findAll()
                .stream()
                .map(mapper::toCategoryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return mapper.toCategoryResponse(findCategoryById(id));
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoriesRepo.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException("Bu kateqoriya artıq mövcuddur");
        }

        return mapper.toCategoryResponse(categoriesRepo.save(mapper.toCategory(request)));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Categories category = findCategoryById(id);

        if (!category.getName().equals(request.getName()) && categoriesRepo.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException("Bu kateqoriya artıq mövcuddur");
        }

        category.setName(request.getName());
        return mapper.toCategoryResponse(categoriesRepo.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoriesRepo.delete(findCategoryById(id));
    }

    private Categories findCategoryById(Long id) {
        return categoriesRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Kateqoriya tapılmadı"));
    }
}
