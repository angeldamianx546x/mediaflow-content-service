package com.mediaflow.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediaflow.api.model.ContentType;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ContentResponse {
    @JsonProperty("content id")
    Integer contentId;

    String format;

    @JsonProperty("file size (MB)")
    Integer fileSizeMB;

    String language;
    String title;

    @JsonProperty("content type")
    ContentType contentType;

    String description;

    @JsonProperty("recommended age")
    Integer recommendedAge;

    @JsonProperty("storage URL")
    String storageUrl;

    @JsonProperty("thumbnail URL")
    String thumbnailUrl;

    LocalDateTime created;

    // Relaciones
    @JsonProperty("location_id")
    Integer locationId;

    @JsonProperty("user_id")
    Integer userId;

    // Metadata de Video
    @JsonProperty("video")
    VideoRespose video;

    // Metadata de Image
    @JsonProperty("image")
    ImageResponse image;

    // Categorías
    @JsonProperty("categories")
    List<CategoryResponse> categories;
}