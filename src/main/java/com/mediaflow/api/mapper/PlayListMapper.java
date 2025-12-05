package com.mediaflow.api.mapper;

import java.util.stream.Collectors;

import com.mediaflow.api.dto.PlayListRequest;
import com.mediaflow.api.dto.PlayListResponse;
import com.mediaflow.api.model.PlayList;

public final class PlayListMapper {

    public static PlayListResponse toResponse(PlayList playList) {
        if (playList == null) {
            return null;
        }

        PlayListResponse.PlayListResponseBuilder builder = PlayListResponse.builder()
                .playListId(playList.getPlayListId())
                .title(playList.getTitle())
                .description(playList.getDescription())
                .isPublic(playList.isPublic())
                .createdAt(playList.getCreatedAt())
                .userId(playList.getUserId());

        if (playList.getContents() != null && !playList.getContents().isEmpty()) {
            builder.contents(
                    playList.getContents().stream()
                            .map(ContentMapper::toResponse)
                            .collect(Collectors.toList()));
        }

        return builder.build();
    }

    public static PlayList toEntity(PlayListRequest dto) {
        if (dto == null) {
            return null;
        }
        return PlayList.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .isPublic(dto.getIsPublic())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public static void copyToEntity(PlayListRequest dto, PlayList entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPublic(dto.getIsPublic());
        entity.setCreatedAt(dto.getCreatedAt());
    }
}