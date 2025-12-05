package com.mediaflow.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mediaflow.api.model.PlayList;

public interface PlayListRepository extends JpaRepository<PlayList, Integer> {

    @Query("SELECT p FROM PlayList p WHERE p.userId = :userId")
    Page<PlayList> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT p FROM PlayList p WHERE p.isPublic = true")
    Page<PlayList> findPublicPlaylists(Pageable pageable);

    @Query("SELECT p FROM PlayList p WHERE p.userId = :userId AND p.isPublic = :isPublic")
    Page<PlayList> findByUserIdAndVisibility(
            @Param("userId") Integer userId,
            @Param("isPublic") boolean isPublic,
            Pageable pageable);
}