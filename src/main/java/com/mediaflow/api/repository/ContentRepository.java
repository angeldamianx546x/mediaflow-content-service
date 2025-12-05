package com.mediaflow.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mediaflow.api.model.Content;
import com.mediaflow.api.model.ContentType;

import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Integer> {

        @Query("SELECT DISTINCT c FROM Content c LEFT JOIN FETCH c.categories WHERE c.userId = :userId AND c.contentType = :contentType")
        Page<Content> findByUserAndContentType(
                        @Param("userId") Integer userId,
                        @Param("contentType") ContentType contentType,
                        Pageable pageable);

        @Query("SELECT DISTINCT c FROM Content c LEFT JOIN FETCH c.categories WHERE c.userId = :userId")
        Page<Content> findByUserContents(
                        @Param("userId") Integer userId,
                        Pageable pageable);

        @Query("SELECT DISTINCT c FROM Content c LEFT JOIN FETCH c.categories WHERE c.contentType = :contentType")
        Page<Content> findByContentType(
                        @Param("contentType") ContentType contentType,
                        Pageable pageable);
}
