package com.mediaflow.api.graphql;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.mediaflow.api.dto.CategoryResponse;
import com.mediaflow.api.dto.ContentRequest;
import com.mediaflow.api.dto.ContentResponse;
import com.mediaflow.api.graphql.input.ContentInput;
import com.mediaflow.api.graphql.input.ContentPage;
import com.mediaflow.api.model.Content;
import com.mediaflow.api.model.ContentType;
import com.mediaflow.api.repository.ContentRepository;
import com.mediaflow.api.service.AuthenticationService;
import com.mediaflow.api.service.ContentService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ContentGraphQLController {

    private final ContentService contentService;
    private final AuthenticationService authenticationService;
    private final ContentRepository contentRepository;

    @QueryMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ContentPage allContents(@Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> result = contentService.findAll(pageable);
        return ContentPage.from(result);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ContentPage contentsByType(@Argument ContentType contentType, @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> result = contentService.findByContentType(contentType, pageable);
        return ContentPage.from(result);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ContentPage myContents(@Argument int page, @Argument int size) {
        Integer currentUserId = authenticationService.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> result = contentService.findByUserFiles(currentUserId, pageable);
        return ContentPage.from(result);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ContentPage myContentsByType(@Argument ContentType contentType, @Argument int page, @Argument int size) {
        Integer currentUserId = authenticationService.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> result = contentService.findByUserContentType(currentUserId, contentType, pageable);
        return ContentPage.from(result);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ContentPage userContents(@Argument Integer userId, @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> result = contentService.findByUserFiles(userId, pageable);
        return ContentPage.from(result);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ContentPage userContentsByType(@Argument Integer userId, @Argument ContentType contentType,
            @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> result = contentService.findByUserContentType(userId, contentType, pageable);
        return ContentPage.from(result);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ContentResponse content(@Argument Integer contentId) {
        return contentService.findById(contentId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public List<CategoryResponse> contentCategories(@Argument Integer contentId) {
        return contentService.getContentCategories(contentId);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ContentResponse createContent(@Argument ContentInput input) {
        Integer currentUserId = authenticationService.getCurrentUserId();
        ContentRequest request = input.toContentRequest();
        request.setUserId(currentUserId);
        return contentService.create(request);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ContentResponse updateContent(@Argument Integer contentId, @Argument ContentInput input) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        if (!authenticationService.canAccess(content.getUserId())) {
            throw new SecurityException("No tienes permiso para actualizar este contenido");
        }

        ContentRequest request = input.toContentRequest();
        return contentService.update(contentId, request);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public Boolean deleteContent(@Argument Integer contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        if (!authenticationService.canAccess(content.getUserId())) {
            throw new SecurityException("No tienes permiso para eliminar este contenido");
        }

        contentService.delete(contentId);
        return true;
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ContentResponse addCategoriesToContent(@Argument Integer contentId, @Argument List<Integer> categoryIds) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        if (!authenticationService.canAccess(content.getUserId())) {
            throw new SecurityException("No tienes permiso para modificar este contenido");
        }

        return contentService.addCategories(contentId, categoryIds);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public Boolean removeCategoryFromContent(@Argument Integer contentId, @Argument Integer categoryId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        if (!authenticationService.canAccess(content.getUserId())) {
            throw new SecurityException("No tienes permiso para modificar este contenido");
        }

        contentService.removeCategory(contentId, categoryId);
        return true;
    }
}
