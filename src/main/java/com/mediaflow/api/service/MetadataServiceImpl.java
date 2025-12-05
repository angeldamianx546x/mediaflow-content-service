package com.mediaflow.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediaflow.api.dto.MetadataRequest;
import com.mediaflow.api.dto.MetadataResponse;
import com.mediaflow.api.mapper.MetadataMapper;
import com.mediaflow.api.model.Content;
import com.mediaflow.api.model.Metadata;
import com.mediaflow.api.repository.ContentRepository;
import com.mediaflow.api.repository.MetadataRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private final MetadataRepository metadataRepository;
    private final ContentRepository contentRepository;

    @Override
    public MetadataResponse findById(Integer metadataId) {
        Metadata metadata = metadataRepository.findById(metadataId)
                .orElseThrow(() -> new EntityNotFoundException("Metadata not found: " + metadataId));
        return MetadataMapper.toResponse(metadata);
    }

    @Override
    public MetadataResponse findByContentId(Integer contentId) {
        Metadata metadata = metadataRepository.findByContentId(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Metadata not found for content: " + contentId));
        return MetadataMapper.toResponse(metadata);
    }

    @Override
    @Transactional
    public MetadataResponse create(MetadataRequest req) {
        Content content = contentRepository.findById(req.getContentId())
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + req.getContentId()));
        
        // Verificar que el contenido no tenga ya metadata
        if (metadataRepository.findByContentId(req.getContentId()).isPresent()) {
            throw new IllegalStateException("Metadata already exists for content: " + req.getContentId());
        }
        
        Metadata metadata = MetadataMapper.toEntity(req);
        metadata.setContent(content);
        
        Metadata saved = metadataRepository.save(metadata);
        return MetadataMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public MetadataResponse update(Integer metadataId, MetadataRequest req) {
        Metadata existing = metadataRepository.findById(metadataId)
                .orElseThrow(() -> new EntityNotFoundException("Metadata not found: " + metadataId));
        
        MetadataMapper.copyToEntity(req, existing);
        
        // Si se proporciona un nuevo contentId
        if (req.getContentId() != null && !req.getContentId().equals(existing.getContent().getContentId())) {
            Content content = contentRepository.findById(req.getContentId())
                    .orElseThrow(() -> new EntityNotFoundException("Content not found: " + req.getContentId()));
            existing.setContent(content);
        }
        
        Metadata saved = metadataRepository.save(existing);
        return MetadataMapper.toResponse(saved);
    }

    @Override
    public void delete(Integer metadataId) {
        if (!metadataRepository.existsById(metadataId)) {
            throw new EntityNotFoundException("Metadata not found: " + metadataId);
        }
        metadataRepository.deleteById(metadataId);
    }
}