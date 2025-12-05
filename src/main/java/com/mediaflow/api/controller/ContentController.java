package com.mediaflow.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mediaflow.api.dto.ContentRequest;
import com.mediaflow.api.dto.ContentResponse;
import com.mediaflow.api.model.Content;
import com.mediaflow.api.model.ContentType;
import com.mediaflow.api.repository.ContentRepository;
import com.mediaflow.api.service.AuthenticationService;
import com.mediaflow.api.service.ContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE,
        RequestMethod.PUT })
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Contents", description = "Content (videos/images) management endpoints")
public class ContentController {

    private final ContentService contentService;
    private final AuthenticationService authenticationService;
    private final ContentRepository contentRepository;

    @Operation(summary = "Get all contents (paginated)", description = "Returns all contents with pagination. Accessible by any authenticated user.")
    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Page<ContentResponse>> getAllContents(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(contentService.findAll(pageable));
    }

    @Operation(summary = "Get contents by type", description = "Returns contents filtered by type (VIDEO or IMAGE) with pagination.")
    @GetMapping("/type/{contentType}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Page<ContentResponse>> getContentsByType(
            @PathVariable ContentType contentType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(contentService.findByContentType(contentType, pageable));
    }

    @Operation(summary = "Get my contents", description = "Returns all contents created by the authenticated user.")
    @GetMapping("/my-contents")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Page<ContentResponse>> getMyContents(
            @PageableDefault(size = 20) Pageable pageable) {
        Integer currentUserId = authenticationService.getCurrentUserId();
        return ResponseEntity.ok(contentService.findByUserFiles(currentUserId, pageable));
    }

    @Operation(summary = "Get my contents by type", description = "Returns contents by type created by the authenticated user.")
    @GetMapping("/my-contents/type/{contentType}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Page<ContentResponse>> getMyContentsByType(
            @PathVariable ContentType contentType,
            @PageableDefault(size = 20) Pageable pageable) {
        Integer currentUserId = authenticationService.getCurrentUserId();
        return ResponseEntity.ok(contentService.findByUserContentType(currentUserId, contentType, pageable));
    }

    @Operation(summary = "Get user contents", description = "Returns all contents from a specific user.")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Page<ContentResponse>> getUserContents(
            @PathVariable Integer userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(contentService.findByUserFiles(userId, pageable));
    }

    @Operation(summary = "Get user contents by type", description = "Returns contents by type from a specific user.")
    @GetMapping("/user/{userId}/type/{contentType}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Page<ContentResponse>> getUserContentsByType(
            @PathVariable Integer userId,
            @PathVariable ContentType contentType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(contentService.findByUserContentType(userId, contentType, pageable));
    }

    @Operation(summary = "Get content by ID", description = "Returns a specific content. Accessible by any authenticated user.")
    @GetMapping("/{contentId}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<ContentResponse> getContentById(@PathVariable Integer contentId) {
        return ResponseEntity.ok(contentService.findById(contentId));
    }

    @Operation(summary = "Create new content", description = "Creates new content (video or image) with metadata and categories. Only creators can upload content.")
    @PostMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> createContent(@Valid @RequestBody ContentRequest request) {
        // El usuario autenticado es el que crea el contenido
        Integer currentUserId = authenticationService.getCurrentUserId();
        request.setUserId(currentUserId);

        ContentResponse created = contentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Get content categories", description = "Returns the categories assigned to a content.")
    @GetMapping("/{contentId}/categories")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<?> getContentCategories(@PathVariable Integer contentId) {
        return ResponseEntity.ok(contentService.getContentCategories(contentId));
    }

    @Operation(summary = "Add categories to content", description = "Adds categories to existing content. Only owner or admin.")
    @PostMapping("/{contentId}/categories")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> addCategoriesToContent(
            @PathVariable Integer contentId,
            @RequestBody java.util.List<Integer> categoryIds) {

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        if (!authenticationService.canAccess(content.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para modificar este contenido"));
        }

        return ResponseEntity.ok(contentService.addCategories(contentId, categoryIds));
    }

    @Operation(summary = "Remove category from content", description = "Removes a category from content. Only owner or admin.")
    @DeleteMapping("/{contentId}/categories/{categoryId}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> removeCategoryFromContent(
            @PathVariable Integer contentId,
            @PathVariable Integer categoryId) {

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        if (!authenticationService.canAccess(content.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para modificar este contenido"));
        }

        contentService.removeCategory(contentId, categoryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update content", description = "Updates content. Only the owner or admin can update.")
    @PutMapping("/{contentId}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> updateContent(
            @PathVariable Integer contentId,
            @Valid @RequestBody ContentRequest request) {

        // Verificar que el contenido existe y obtener su propietario
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        // Validar que solo el propietario o admin puede actualizar
        if (!authenticationService.canAccess(content.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para actualizar este contenido"));
        }

        return ResponseEntity.ok(contentService.update(contentId, request));
    }

    @Operation(summary = "Delete content", description = "Deletes content. Only the owner or admin can delete.")
    @DeleteMapping("/{contentId}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> deleteContent(@PathVariable Integer contentId) {
        // Verificar que el contenido existe y obtener su propietario
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        // Validar que solo el propietario o admin puede eliminar
        if (!authenticationService.canAccess(content.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para eliminar este contenido"));
        }

        contentService.delete(contentId);
        return ResponseEntity.noContent().build();
    }

    private java.util.Map<String, Object> buildErrorResponse(String message) {
        java.util.Map<String, Object> error = new java.util.HashMap<>();
        error.put("timestamp", java.time.Instant.now().toString());
        error.put("code", "FORBIDDEN");
        error.put("message", message);
        return error;
    }
}