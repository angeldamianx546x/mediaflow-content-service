package com.mediaflow.api.mapper;

import com.mediaflow.api.dto.CategoryRequest;
import com.mediaflow.api.dto.CategoryResponse;
import com.mediaflow.api.model.Category;

public final class CategoryMapper {
    
    public static CategoryResponse toResponse(Category category) {
        if (category == null)
            return null;
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescrition())
                .build();
    }

    public static Category toEntity(CategoryRequest dto) {
        if (dto == null)
            return null;
        return Category.builder()
                .name(dto.getName())
                .descrition(dto.getDescription())
                .build();
    }

    public static void copyToEntity(CategoryRequest dto, Category entity) {
        if (dto == null || entity == null)
            return;
        entity.setName(dto.getName());
        entity.setDescrition(dto.getDescription());
    }
}