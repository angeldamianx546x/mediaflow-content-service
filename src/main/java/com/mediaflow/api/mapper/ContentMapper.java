package com.mediaflow.api.mapper;

import java.util.stream.Collectors;

import com.mediaflow.api.dto.ContentRequest;
import com.mediaflow.api.dto.ContentResponse;
import com.mediaflow.api.model.Content;

public final class ContentMapper {

    public static ContentResponse toResponse(Content content) {
        if (content == null) {
            return null;
        }
        ContentResponse.ContentResponseBuilder builder = ContentResponse.builder()
                .contentId(content.getContentId())
                .format(content.getFormat())
                .fileSizeMB(content.getFileSizeMB())
                .language(content.getLanguage())
                .title(content.getTitle())
                .contentType(content.getContentType())
                .description(content.getDescription())
                .recommendedAge(content.getRecommendedAge())
                .storageUrl(content.getStorageUrl())
                .thumbnailUrl(content.getThumbnailUrl())
                .created(content.getCreated())
                .locationId(content.getLocationId())
                .userId(content.getUserId());

        if (content.getVideo() != null) {
            builder.video(VideoMapper.toResponse(content.getVideo()));
        }
        if (content.getImage() != null) {
            builder.image(ImageMapper.toResponse(content.getImage()));
        }
        if (content.getCategories() != null && !content.getCategories().isEmpty()) {
            builder.categories(
                    content.getCategories().stream()
                            .map(CategoryMapper::toResponse)
                            .collect(Collectors.toList()));
        }

        return builder.build();
    }

    public static Content toEntity(ContentRequest dto) {
        if (dto == null) {
            return null;
        }

        return Content.builder()
                .format(dto.getFormat())
                .fileSizeMB(dto.getFileSizeMB())
                .language(dto.getLanguage())
                .title(dto.getTitle())
                .contentType(dto.getContentType())
                .description(dto.getDescription())
                .recommendedAge(dto.getRecommendedAge())
                .storageUrl(dto.getStorageUrl())
                .thumbnailUrl(dto.getThumbnailUrl())
                .created(dto.getCreated())
                .build();
    }

    public static void copyToEntity(ContentRequest dto, Content entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setFormat(dto.getFormat());
        entity.setFileSizeMB(dto.getFileSizeMB());
        entity.setLanguage(dto.getLanguage());
        entity.setTitle(dto.getTitle());
        entity.setContentType(dto.getContentType());
        entity.setDescription(dto.getDescription());
        entity.setRecommendedAge(dto.getRecommendedAge());
        entity.setStorageUrl(dto.getStorageUrl());
        entity.setThumbnailUrl(dto.getThumbnailUrl());
        entity.setCreated(dto.getCreated());
    }
}
