package com.mediaflow.api.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MetadataResponse {
    @JsonProperty("metadata Id")
    Integer metadataId;
    
    String extractor;
    
    @JsonProperty("result json")
    String resultJson;
    
    @JsonProperty("extracted at")
    LocalDate extractedAt;
    
    @JsonProperty("content id")
    Integer contentId;
}