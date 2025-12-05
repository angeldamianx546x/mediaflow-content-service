package com.mediaflow.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryResponse {
    @JsonProperty("category Id")
    Integer categoryId;
    String name;
    String description;
}
