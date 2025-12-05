package com.mediaflow.api.graphql.input;

import java.time.LocalDateTime;
import java.util.List;

import com.mediaflow.api.dto.ContentRequest;
import com.mediaflow.api.dto.ImageRequest;
import com.mediaflow.api.dto.VideoRequet;
import com.mediaflow.api.model.ContentType;

import lombok.Data;

@Data
public class ContentInput {
    private String format;
    private Integer fileSizeMB;
    private String language;
    private String title;
    private ContentType contentType;
    private String description;
    private Integer recommendedAge;
    private String storageUrl;
    private String thumbnailUrl;
    private LocalDateTime created;
    private Integer locationId;
    private List<Integer> categoryIds;
    private VideoInput videoMetadata;
    private ImageInput imageMetadata;

    public ContentRequest toContentRequest() {
        ContentRequest req = new ContentRequest();
        req.setFormat(this.format);
        req.setFileSizeMB(this.fileSizeMB);
        req.setLanguage(this.language);
        req.setTitle(this.title);
        req.setContentType(this.contentType);
        req.setDescription(this.description);
        req.setRecommendedAge(this.recommendedAge);
        req.setStorageUrl(this.storageUrl);
        req.setThumbnailUrl(this.thumbnailUrl);
        req.setCreated(this.created);
        req.setLocationId(this.locationId);
        req.setCategoryIds(this.categoryIds);
        
        if (this.videoMetadata != null) {
            VideoRequet videoReq = new VideoRequet();
            videoReq.setDurationSeconds(this.videoMetadata.getDurationSeconds());
            videoReq.setWidth(this.videoMetadata.getWidth());
            videoReq.setHeight(this.videoMetadata.getHeight());
            req.setVideoMetadata(videoReq);
        }
        
        if (this.imageMetadata != null) {
            ImageRequest imageReq = new ImageRequest();
            imageReq.setWidth(this.imageMetadata.getWidth());
            imageReq.setHeight(this.imageMetadata.getHeight());
            req.setImageMetadata(imageReq);
        }
        
        return req;
    }
}
