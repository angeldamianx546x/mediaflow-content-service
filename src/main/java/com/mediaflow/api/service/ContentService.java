package com.mediaflow.api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mediaflow.api.dto.CategoryResponse;
import com.mediaflow.api.dto.ContentRequest;
import com.mediaflow.api.dto.ContentResponse;
import com.mediaflow.api.model.ContentType;

public interface ContentService {
    Page<ContentResponse> findAll(Pageable pageable);
    
    Page<ContentResponse> findByContentType(ContentType contentType, Pageable pageable);
    
    Page<ContentResponse> findByUserContentType(Integer userId, ContentType contentType, Pageable pageable);
    
    Page<ContentResponse> findByUserFiles(Integer userId, Pageable pageable);

    ContentResponse findById(Integer contentId);

    ContentResponse create(ContentRequest req);

    ContentResponse update(Integer contentId, ContentRequest req);

    void delete(Integer contentId);
    
    List<CategoryResponse> getContentCategories(Integer contentId);
    
    ContentResponse addCategories(Integer contentId, List<Integer> categoryIds);
    
    void removeCategory(Integer contentId, Integer categoryId);
}
