package com.mediaflow.api.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlayListRequest {
    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotNull
    private Boolean isPublic;

    @NotNull
    private LocalDate createdAt;

    private Integer userId; // Se asigna automáticamente desde el token

    private List<Integer> contentIds;
}