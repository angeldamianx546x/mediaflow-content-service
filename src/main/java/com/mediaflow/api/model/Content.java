package com.mediaflow.api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Integer contentId;

    @Column(name = "format", nullable = false, length = 6)
    private String format;

    @Column(name = "file_size_mb", nullable = false)
    private Integer fileSizeMB;

    @Column(name = "language", nullable = false, length = 20)
    private String language;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "recommended_age")
    private Integer recommendedAge;

    @Column(name = "storage_url", nullable = false, columnDefinition = "TEXT")
    private String storageUrl;

    @Column(name = "thumbnail_url", nullable = false, columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "user_id")
    private Integer userId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "video_id", referencedColumnName = "video_id")
    private Video video;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "image_id")
    private Image image;

    @ManyToMany(mappedBy = "contents")
    private List<Category> categories = new ArrayList<>();

    @ManyToMany(mappedBy = "contents")
    @Builder.Default
    private List<PlayList> playLists = new ArrayList<>();

    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Metadata metadata;
}
