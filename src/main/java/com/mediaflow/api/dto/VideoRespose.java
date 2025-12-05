package com.mediaflow.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VideoRespose {
    @JsonProperty("video Id")
    Integer videoId;
    Integer durationSeconds;
    Integer width;
    Integer height;
}
