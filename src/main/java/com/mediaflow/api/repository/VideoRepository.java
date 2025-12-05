package com.mediaflow.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mediaflow.api.model.Video;

public interface VideoRepository extends JpaRepository<Video, Integer>{
    
}
