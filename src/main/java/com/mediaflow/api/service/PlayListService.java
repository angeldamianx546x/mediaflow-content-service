package com.mediaflow.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mediaflow.api.dto.PlayListRequest;
import com.mediaflow.api.dto.PlayListResponse;

public interface PlayListService {
    Page<PlayListResponse> findAll(Pageable pageable);
    
    Page<PlayListResponse> findByUserId(Integer userId, Pageable pageable);
    
    Page<PlayListResponse> findPublicPlaylists(Pageable pageable);
    
    Page<PlayListResponse> findByUserIdAndVisibility(Integer userId, boolean isPublic, Pageable pageable);
    
    PlayListResponse findById(Integer playlistId);
    
    PlayListResponse create(PlayListRequest req);
    
    PlayListResponse update(Integer playlistId, PlayListRequest req);
    
    void delete(Integer playlistId);
    
    PlayListResponse addContentToPlaylist(Integer playlistId, Integer contentId);
    
    PlayListResponse addMultipleContents(Integer playlistId, java.util.List<Integer> contentIds);
    
    void removeContentFromPlaylist(Integer playlistId, Integer contentId);
}