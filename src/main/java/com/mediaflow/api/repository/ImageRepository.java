package com.mediaflow.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mediaflow.api.model.Image;

public interface ImageRepository extends JpaRepository<Image, Integer>{
    
}
