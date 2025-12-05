package com.mediaflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    @Size(max = 70)
    private String name;

    @NotBlank
    @Size(max = 250)
    private String description;
}
