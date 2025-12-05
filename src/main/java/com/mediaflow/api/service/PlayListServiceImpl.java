package com.mediaflow.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediaflow.api.dto.PlayListRequest;
import com.mediaflow.api.dto.PlayListResponse;
import com.mediaflow.api.mapper.PlayListMapper;
import com.mediaflow.api.model.Content;
import com.mediaflow.api.model.PlayList;
import com.mediaflow.api.repository.ContentRepository;
import com.mediaflow.api.repository.PlayListRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayListServiceImpl implements PlayListService {

    private final PlayListRepository playListRepository;
    private final ContentRepository contentRepository;

    @Override
    public Page<PlayListResponse> findAll(Pageable pageable) {
        Page<PlayList> playlists = playListRepository.findAll(pageable);
        return playlists.map(PlayListMapper::toResponse);
    }

    @Override
    public Page<PlayListResponse> findByUserId(Integer userId, Pageable pageable) {
        Page<PlayList> playlists = playListRepository.findByUserId(userId, pageable);
        return playlists.map(PlayListMapper::toResponse);
    }

    @Override
    public Page<PlayListResponse> findPublicPlaylists(Pageable pageable) {
        Page<PlayList> playlists = playListRepository.findPublicPlaylists(pageable);
        return playlists.map(PlayListMapper::toResponse);
    }

    @Override
    public Page<PlayListResponse> findByUserIdAndVisibility(Integer userId, boolean isPublic, Pageable pageable) {
        Page<PlayList> playlists = playListRepository.findByUserIdAndVisibility(userId, isPublic, pageable);
        return playlists.map(PlayListMapper::toResponse);
    }

    @Override
    public PlayListResponse findById(Integer playlistId) {
        PlayList playlist = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));
        return PlayListMapper.toResponse(playlist);
    }

    @Override
    @Transactional
    public PlayListResponse create(PlayListRequest req) {
        PlayList playlist = PlayListMapper.toEntity(req);
        playlist.setUserId(req.getUserId());

        // Agregar contenidos si se proporcionaron
        if (req.getContentIds() != null && !req.getContentIds().isEmpty()) {
            List<Content> contents = new ArrayList<>();
            for (Integer contentId : req.getContentIds()) {
                Content content = contentRepository.findById(contentId)
                        .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));
                contents.add(content);
            }
            playlist.setContents(contents);
        }

        PlayList saved = playListRepository.save(playlist);
        return PlayListMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PlayListResponse update(Integer playlistId, PlayListRequest req) {
        PlayList existing = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));

        PlayListMapper.copyToEntity(req, existing);

        // Actualizar usuario si se proporciona
        if (req.getUserId() != null) {
            existing.setUserId(req.getUserId());
        }

        // Actualizar contenidos si se proporcionan
        if (req.getContentIds() != null) {
            List<Content> contents = new ArrayList<>();
            for (Integer contentId : req.getContentIds()) {
                Content content = contentRepository.findById(contentId)
                        .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));
                contents.add(content);
            }
            existing.setContents(contents);
        }

        PlayList saved = playListRepository.save(existing);
        return PlayListMapper.toResponse(saved);
    }

    @Override
    public void delete(Integer playlistId) {
        if (!playListRepository.existsById(playlistId)) {
            throw new EntityNotFoundException("Playlist not found: " + playlistId);
        }
        playListRepository.deleteById(playlistId);
    }

    @Override
    @Transactional
    public PlayListResponse addContentToPlaylist(Integer playlistId, Integer contentId) {
        PlayList playlist = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        if (!playlist.getContents().contains(content)) {
            playlist.getContents().add(content);
        }

        PlayList saved = playListRepository.save(playlist);
        return PlayListMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PlayListResponse addMultipleContents(Integer playlistId, List<Integer> contentIds) {
        PlayList playlist = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));

        for (Integer contentId : contentIds) {
            Content content = contentRepository.findById(contentId)
                    .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

            if (!playlist.getContents().contains(content)) {
                playlist.getContents().add(content);
            }
        }

        PlayList saved = playListRepository.save(playlist);
        return PlayListMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void removeContentFromPlaylist(Integer playlistId, Integer contentId) {
        PlayList playlist = playListRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found: " + playlistId));

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        playlist.getContents().remove(content);
        playListRepository.save(playlist);
    }
}