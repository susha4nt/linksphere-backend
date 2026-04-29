package com.linksphere.platform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false, unique = true)
    private String shortCode;

    private LocalDateTime createdAt = LocalDateTime.now(); // ✅ auto set

    private Long clickCount = 0L; // 🔥 NEW: analytics

    private LocalDateTime expiryDate; // 🔥 NEW: expiry
}