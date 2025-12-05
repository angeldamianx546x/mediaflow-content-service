package com.mediaflow.api.service;

import com.mediaflow.api.dto.MetadataRequest;
import com.mediaflow.api.dto.MetadataResponse;

public interface MetadataService {
    MetadataResponse findById(Integer metadataId);
    
    MetadataResponse findByContentId(Integer contentId);
    
    MetadataResponse create(MetadataRequest req);
    
    MetadataResponse update(Integer metadataId, MetadataRequest req);
    
    void delete(Integer metadataId);
}