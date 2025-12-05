package com.mediaflow.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediaflow.api.dto.CategoryResponse;
import com.mediaflow.api.dto.ContentRequest;
import com.mediaflow.api.dto.ContentResponse;
import com.mediaflow.api.mapper.CategoryMapper;
import com.mediaflow.api.mapper.ContentMapper;
import com.mediaflow.api.mapper.ImageMapper;
import com.mediaflow.api.mapper.VideoMapper;
import com.mediaflow.api.model.Category;
import com.mediaflow.api.model.Content;
import com.mediaflow.api.model.ContentType;
import com.mediaflow.api.model.Image;
import com.mediaflow.api.model.Video;
import com.mediaflow.api.repository.CategoryRepository;
import com.mediaflow.api.repository.ContentRepository;
import com.mediaflow.api.repository.ImageRepository;
import com.mediaflow.api.repository.VideoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final VideoRepository videoRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ContentResponse> findAll(Pageable pageable) {
        Page<Content> contents = contentRepository.findAll(pageable);
        return contents.map(ContentMapper::toResponse);
    }

    @Override
    public Page<ContentResponse> findByContentType(ContentType contentType, Pageable pageable) {
        Page<Content> contents = contentRepository.findByContentType(contentType, pageable);
        return contents.map(ContentMapper::toResponse);
    }

    @Override
    public Page<ContentResponse> findByUserContentType(Integer userId, ContentType contentType, Pageable pageable) {
        Page<Content> contents = contentRepository.findByUserAndContentType(userId, contentType, pageable);
        return contents.map(ContentMapper::toResponse);
    }

    @Override
    public Page<ContentResponse> findByUserFiles(Integer userId, Pageable pageable) {
        Page<Content> contents = contentRepository.findByUserContents(userId, pageable);
        return contents.map(ContentMapper::toResponse);
    }

    @Override
    public ContentResponse findById(Integer contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));
        return ContentMapper.toResponse(content);
    }

    @Override
    @Transactional
    public ContentResponse create(ContentRequest req) {
        // Convertir el DTO en entidad
        Content content = ContentMapper.toEntity(req);
        content.setUserId(req.getUserId());
        content.setLocationId(req.getLocationId());

        // Procesar Video o Image según el tipo de contenido
        if (req.getContentType() == ContentType.VIDEO && req.getVideoMetadata() != null) {
            Video video = VideoMapper.toEntity(req.getVideoMetadata());
            video = videoRepository.save(video);
            content.setVideo(video);
        } else if (req.getContentType() == ContentType.IMAGE && req.getImageMetadata() != null) {
            Image image = ImageMapper.toEntity(req.getImageMetadata());
            image = imageRepository.save(image);
            content.setImage(image);
        }

        // Agregar categorías si se proporcionaron
        if (req.getCategoryIds() != null && !req.getCategoryIds().isEmpty()) {
            List<Category> categories = new ArrayList<>();
            for (Integer categoryId : req.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
                categories.add(category);
            }
            content.setCategories(categories);
        }

        // Guardar el contenido
        Content saved = contentRepository.save(content);

        // Devolver respuesta
        return ContentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ContentResponse update(Integer contentId, ContentRequest req) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        ContentMapper.copyToEntity(req, content);

        // Si se envía un nuevo userId
        if (req.getUserId() != null) {
            content.setUserId(req.getUserId());
        }

        // Si se envía un nuevo locationId
        if (req.getLocationId() != null) {
            content.setLocationId(req.getLocationId());
        }

        // Actualizar metadata de video si existe
        if (req.getVideoMetadata() != null && content.getVideo() != null) {
            VideoMapper.copyToEntity(req.getVideoMetadata(), content.getVideo());
            videoRepository.save(content.getVideo());
        }

        // Actualizar metadata de imagen si existe
        if (req.getImageMetadata() != null && content.getImage() != null) {
            ImageMapper.copyToEntity(req.getImageMetadata(), content.getImage());
            imageRepository.save(content.getImage());
        }

        // Actualizar categorías si se proporcionaron
        if (req.getCategoryIds() != null) {
            List<Category> categories = new ArrayList<>();
            for (Integer categoryId : req.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
                categories.add(category);
            }
            content.setCategories(categories);
        }

        Content updated = contentRepository.save(content);
        return ContentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Integer contentId) {
        if (!contentRepository.existsById(contentId)) {
            throw new EntityNotFoundException("Content not found: " + contentId);
        }
        contentRepository.deleteById(contentId);
    }

    @Override
    public List<CategoryResponse> getContentCategories(Integer contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        return content.getCategories().stream()
                .map(CategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContentResponse addCategories(Integer contentId, List<Integer> categoryIds) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        for (Integer categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));

            if (!content.getCategories().contains(category)) {
                content.getCategories().add(category);
            }
        }

        Content updated = contentRepository.save(content);
        return ContentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void removeCategory(Integer contentId, Integer categoryId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + contentId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));

        content.getCategories().remove(category);
        contentRepository.save(content);
    }
}
