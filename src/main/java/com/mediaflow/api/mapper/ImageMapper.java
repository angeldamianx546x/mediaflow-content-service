package com.mediaflow.api.mapper;

import com.mediaflow.api.dto.ImageRequest;
import com.mediaflow.api.dto.ImageResponse;
import com.mediaflow.api.model.Image;

public final class ImageMapper {
    
    public static ImageResponse toResponse(Image image) {
        if (image == null)
            return null;
        return ImageResponse.builder()
                .imageId(image.getImageId())
                .width(image.getWidth())
                .height(image.getHeight())
                .build();
    }

    public static Image toEntity(ImageRequest dto) {
        if (dto == null)
            return null;
        return Image.builder()
                .width(dto.getWidth())
                .height(dto.getHeight())
                .build();
    }

    public static void copyToEntity(ImageRequest dto, Image entity) {
        if (dto == null || entity == null)
            return;
        entity.setWidth(dto.getWidth());
        entity.setHeight(dto.getHeight());
    }
}