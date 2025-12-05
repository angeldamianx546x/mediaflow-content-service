package com.mediaflow.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mediaflow.api.model.Metadata;

public interface MetadataRepository extends JpaRepository<Metadata, Integer> {
    
    @Query("SELECT m FROM Metadata m WHERE m.content.contentId = :contentId")
    Optional<Metadata> findByContentId(@Param("contentId") Integer contentId);
}
