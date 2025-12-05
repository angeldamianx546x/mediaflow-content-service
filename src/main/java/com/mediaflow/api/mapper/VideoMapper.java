package com.mediaflow.api.mapper;

import com.mediaflow.api.dto.VideoRequet;
import com.mediaflow.api.dto.VideoRespose;
import com.mediaflow.api.model.Video;

public class VideoMapper {
    public static VideoRespose toResponse(Video video) {
        if (video == null)
            return null;
        return VideoRespose.builder()
                .videoId(video.getVideoId())
                .width(video.getWidth())
                .height(video.getHeight())
                .durationSeconds(video.getDurationSeconds())
                .build();
    }

    public static Video toEntity(VideoRequet dto) {
        if (dto == null)
            return null;
        return Video.builder()
                .width(dto.getWidth())
                .height(dto.getHeight())
                .durationSeconds(dto.getDurationSeconds())
                .build();
    }

    public static void copyToEntity(VideoRequet dto, Video entity) {
        if (dto == null || entity == null)
            return;
        entity.setWidth(dto.getWidth());
        entity.setHeight(dto.getHeight());
        entity.setDurationSeconds(dto.getDurationSeconds());
    }
}
