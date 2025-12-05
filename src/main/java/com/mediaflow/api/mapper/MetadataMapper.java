
package com.mediaflow.api.mapper;

import com.mediaflow.api.dto.MetadataRequest;
import com.mediaflow.api.dto.MetadataResponse;
import com.mediaflow.api.model.Metadata;

public final class MetadataMapper {
    
    public static MetadataResponse toResponse(Metadata metadata) {
        if (metadata == null)
            return null;
        
        MetadataResponse.MetadataResponseBuilder builder = MetadataResponse.builder()
                .metadataId(metadata.getMetadataId())
                .extractor(metadata.getExtractor())
                .resultJson(metadata.getResultJson())
                .extractedAt(metadata.getExtractedAt());
        
        if (metadata.getContent() != null) {
            builder.contentId(metadata.getContent().getContentId());
        }
        
        return builder.build();
    }

    public static Metadata toEntity(MetadataRequest dto) {
        if (dto == null)
            return null;
        return Metadata.builder()
                .extractor(dto.getExtractor())
                .resultJson(dto.getResultJson())
                .extractedAt(dto.getExtractedAt())
                .build();
    }

    public static void copyToEntity(MetadataRequest dto, Metadata entity) {
        if (dto == null || entity == null)
            return;
        entity.setExtractor(dto.getExtractor());
        entity.setResultJson(dto.getResultJson());
        entity.setExtractedAt(dto.getExtractedAt());
    }
}