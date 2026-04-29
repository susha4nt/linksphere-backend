package com.linksphere.platform.DTO;

import lombok.Data;

@Data
public class UrlRequest {

    private String url;

    private Integer expiryMinutes; // ✅ optional
}