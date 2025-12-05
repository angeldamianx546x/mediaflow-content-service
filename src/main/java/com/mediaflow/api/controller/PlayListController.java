package com.mediaflow.api.controller;

import java.net.URI;
import java.util.List;

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

import com.mediaflow.api.dto.PlayListRequest;
import com.mediaflow.api.dto.PlayListResponse;
import com.mediaflow.api.model.PlayList;
import com.mediaflow.api.repository.PlayListRepository;
import com.mediaflow.api.service.AuthenticationService;
import com.mediaflow.api.service.PlayListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/playlists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE,
        RequestMethod.PUT })
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Playlists", description = "Playlist management endpoints")
public class PlayListController {

    private final PlayListService playListService;
    private final AuthenticationService authenticationService;
    private final PlayListRepository playListRepository;

    @Operation(summary = "Get all public playlists", description = "Returns all public playlists with pagination")
    @GetMapping("/public")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Page<PlayListResponse>> getPublicPlaylists(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(playListService.findPublicPlaylists(pageable));
    }

    @Operation(summary = "Get my playlists", description = "Returns playlists created by the authenticated user")
    @GetMapping("/my-playlists")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Page<PlayListResponse>> getMyPlaylists(
            @PageableDefault(size = 20) Pageable pageable) {
        Integer currentUserId = authenticationService.getCurrentUserId();
        return ResponseEntity.ok(playListService.findByUserId(currentUserId, pageable));
    }

    @Operation(summary = "Get playlists by user", description = "Returns playlists from a specific user")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Page<PlayListResponse>> getPlaylistsByUser(
            @PathVariable Integer userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(playListService.findByUserId(userId, pageable));
    }

    @Operation(summary = "Get playlist by ID", description = "Returns a specific playlist")
    @GetMapping("/{playlistId}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<?> getPlaylistById(@PathVariable Integer playlistId) {
        PlayList playlist = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));

        // Verificar si la playlist es pública o pertenece al usuario actual
        Integer currentUserId = authenticationService.getCurrentUserId();
        if (!playlist.isPublic() && !authenticationService.canAccess(playlist.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para ver esta playlist"));
        }

        return ResponseEntity.ok(playListService.findById(playlistId));
    }

    @Operation(summary = "Create new playlist", description = "Creates a new playlist")
    @PostMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<PlayListResponse> createPlaylist(@Valid @RequestBody PlayListRequest request) {
        Integer currentUserId = authenticationService.getCurrentUserId();
        request.setUserId(currentUserId);

        PlayListResponse created = playListService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/playlists/" + created.getPlayListId()))
                .body(created);
    }

    @Operation(summary = "Update playlist", description = "Updates a playlist. Only the owner or admin can update.")
    @PutMapping("/{playlistId}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<?> updatePlaylist(
            @PathVariable Integer playlistId,
            @Valid @RequestBody PlayListRequest request) {

        PlayList playlist = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));

        if (!authenticationService.canAccess(playlist.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para actualizar esta playlist"));
        }

        return ResponseEntity.ok(playListService.update(playlistId, request));
    }

    @Operation(summary = "Delete playlist", description = "Deletes a playlist. Only the owner or admin can delete.")
    @DeleteMapping("/{playlistId}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<?> deletePlaylist(@PathVariable Integer playlistId) {
        PlayList playlist = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));

        if (!authenticationService.canAccess(playlist.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para eliminar esta playlist"));
        }

        playListService.delete(playlistId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add content to playlist", description = "Adds a single content to a playlist")
    @PostMapping("/{playlistId}/contents/{contentId}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<?> addContentToPlaylist(
            @PathVariable Integer playlistId,
            @PathVariable Integer contentId) {

        PlayList playlist = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));

        if (!authenticationService.canAccess(playlist.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para modificar esta playlist"));
        }

        return ResponseEntity.ok(playListService.addContentToPlaylist(playlistId, contentId));
    }

    @Operation(summary = "Add multiple contents to playlist", description = "Adds multiple contents to a playlist")
    @PostMapping("/{playlistId}/contents")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<?> addMultipleContents(
            @PathVariable Integer playlistId,
            @RequestBody List<Integer> contentIds) {

        PlayList playlist = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));

        if (!authenticationService.canAccess(playlist.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para modificar esta playlist"));
        }

        return ResponseEntity.ok(playListService.addMultipleContents(playlistId, contentIds));
    }

    @Operation(summary = "Remove content from playlist", description = "Removes a content from a playlist")
    @DeleteMapping("/{playlistId}/contents/{contentId}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<?> removeContentFromPlaylist(
            @PathVariable Integer playlistId,
            @PathVariable Integer contentId) {

        PlayList playlist = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));

        if (!authenticationService.canAccess(playlist.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para modificar esta playlist"));
        }

        playListService.removeContentFromPlaylist(playlistId, contentId);
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