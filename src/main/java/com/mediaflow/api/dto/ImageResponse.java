package com.mediaflow.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImageResponse {
    @JsonProperty("image Id")
    Integer imageId;
    Integer width;
    Integer height;
}
