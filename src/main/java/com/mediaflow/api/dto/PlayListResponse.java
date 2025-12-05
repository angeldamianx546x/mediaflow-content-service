package com.mediaflow.api.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlayListResponse {
    @JsonProperty("playlist Id")
    Integer playListId;

    String title;

    String description;

    @JsonProperty("is public")
    Boolean isPublic;

    @JsonProperty("created at")
    LocalDate createdAt;

    @JsonProperty("user_id")
    Integer userId;

    @JsonProperty("contents")
    List<ContentResponse> contents;
}
