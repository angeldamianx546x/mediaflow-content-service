package com.mediaflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoRequet {
    @NotBlank
    Integer durationSeconds;
    @NotBlank
    Integer width;
    @NotBlank
    Integer height;
}
