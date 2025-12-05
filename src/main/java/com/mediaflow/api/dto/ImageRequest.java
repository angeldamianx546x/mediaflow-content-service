package com.mediaflow.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImageRequest {
    @NotNull
    @Min(1)
    private Integer width;
    
    @NotNull
    @Min(1)
    private Integer height;
}
