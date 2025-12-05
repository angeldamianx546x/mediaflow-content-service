package com.mediaflow.api.controller;

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

import com.mediaflow.api.dto.MetadataRequest;
import com.mediaflow.api.dto.MetadataResponse;
import com.mediaflow.api.model.Content;
import com.mediaflow.api.repository.ContentRepository;
import com.mediaflow.api.service.AuthenticationService;
import com.mediaflow.api.service.MetadataService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/metadata")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE,
        RequestMethod.PUT })
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Metadata", description = "Content metadata extraction and management endpoints")
public class MetadataController {

    private final MetadataService metadataService;
    private final AuthenticationService authenticationService;
    private final ContentRepository contentRepository;

    @Operation(summary = "Get metadata by content", description = "Returns the metadata for a specific content")
    @GetMapping("/content/{contentId}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<MetadataResponse> getMetadataByContent(@PathVariable Integer contentId) {
        return ResponseEntity.ok(metadataService.findByContentId(contentId));
    }

    @Operation(summary = "Get metadata by ID", description = "Returns a specific metadata by ID")
    @GetMapping("/{metadataId}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<MetadataResponse> getMetadataById(@PathVariable Integer metadataId) {
        return ResponseEntity.ok(metadataService.findById(metadataId));
    }

    @Operation(summary = "Create metadata for content", description = "Creates metadata for content. Only accessible by content owner or admin.")
    @PostMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> createMetadata(@Valid @RequestBody MetadataRequest request) {
        Content content = contentRepository.findById(request.getContentId())
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + request.getContentId()));

        if (!authenticationService.canAccess(content.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para crear metadata para este contenido"));
        }

        MetadataResponse created = metadataService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update metadata", description = "Updates metadata. Only accessible by content owner or admin.")
    @PutMapping("/{metadataId}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> updateMetadata(
            @PathVariable Integer metadataId,
            @Valid @RequestBody MetadataRequest request) {

        MetadataResponse metadata = metadataService.findById(metadataId);
        Content content = contentRepository.findById(metadata.getContentId())
                .orElseThrow(() -> new EntityNotFoundException("Content not found"));

        if (!authenticationService.canAccess(content.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para actualizar esta metadata"));
        }

        return ResponseEntity.ok(metadataService.update(metadataId, request));
    }

    @Operation(summary = "Delete metadata", description = "Deletes metadata. Only accessible by content owner or admin.")
    @DeleteMapping("/{metadataId}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> deleteMetadata(@PathVariable Integer metadataId) {
        MetadataResponse metadata = metadataService.findById(metadataId);
        Content content = contentRepository.findById(metadata.getContentId())
                .orElseThrow(() -> new EntityNotFoundException("Content not found"));

        if (!authenticationService.canAccess(content.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para eliminar esta metadata"));
        }

        metadataService.delete(metadataId);
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