package com.mediaflow.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mediaflow.api.model.ContentType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContentRequest {
    @NotBlank
    @Size(max = 6)
    private String format;

    @NotNull
    private Integer fileSizeMB;

    @NotBlank
    @Size(max = 20)
    private String language;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotNull
    private ContentType contentType; 

    private String description;

    @Min(0)
    private Integer recommendedAge;

    @NotBlank
    private String storageUrl;

    @NotBlank
    private String thumbnailUrl;

    @NotNull
    private LocalDateTime created;

    // Relaciones (IDs)
    private Integer userId; // Se asigna automáticamente desde el token

    private Integer locationId;

    // IDs de categorías
    private List<Integer> categoryIds;

    // Metadata para Video
    private VideoRequet videoMetadata;

    // Metadata para Image
    private ImageRequest imageMetadata;
}
