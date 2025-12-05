package com.mediaflow.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MetadataRequest {
    @NotBlank
    @Size(max = 120)
    private String extractor;

    @NotBlank
    private String resultJson; // JSON en formato String

    @NotNull
    private LocalDate extractedAt;

    @NotNull
    private Integer contentId;
}
