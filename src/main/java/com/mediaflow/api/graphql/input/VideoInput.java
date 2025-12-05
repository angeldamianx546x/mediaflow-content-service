package com.mediaflow.api.graphql.input;

import lombok.Data;

@Data
public class VideoInput {
    private Integer durationSeconds;
    private Integer width;
    private Integer height;
}
