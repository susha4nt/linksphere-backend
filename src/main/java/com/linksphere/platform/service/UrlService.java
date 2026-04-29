package com.linksphere.platform.service;

import com.linksphere.platform.DTO.UrlRequest;
import com.linksphere.platform.entity.UrlMapping;
import com.linksphere.platform.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
public class UrlService {

    @Autowired
    private UrlRepository repository;

    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    // ✅ Generate unique short code
    public String generateShortCode() {
        String code;

        do {
            StringBuilder sb = new StringBuilder();
            Random random = new Random();

            for (int i = 0; i < 6; i++) {
                sb.append(BASE62.charAt(random.nextInt(BASE62.length())));
            }

            code = sb.toString();

        } while (repository.findByShortCode(code).isPresent());

        return code;
    }

    // ✅ Create short URL (with expiry support)
    public String shortenUrl(UrlRequest request) {

        String originalUrl = request.getUrl().replace("\"", "").trim();

        // fix missing protocol
        if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
            originalUrl = "https://" + originalUrl;
        }

        // validate URL
        try {
            new URI(originalUrl);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL: " + originalUrl);
        }

        String shortCode = generateShortCode();

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(originalUrl);
        mapping.setShortCode(shortCode);
        mapping.setCreatedAt(LocalDateTime.now());

        // 🔥 expiry support
        if (request.getExpiryMinutes() != null) {
            mapping.setExpiryDate(LocalDateTime.now().plusMinutes(request.getExpiryMinutes()));
        }

        repository.save(mapping);

        return shortCode;
    }

    // ✅ Redirect logic with click tracking + expiry
    public String getOriginalUrlAndTrack(String code) {

        UrlMapping url = repository.findByShortCode(code)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        // 🔥 expiry check
        if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("EXPIRED");
        }

        // 🔥 increment click count
        url.setClickCount(url.getClickCount() + 1);
        repository.save(url);

        return url.getOriginalUrl();
    }

    // ✅ Analytics API
    public Map<String, Object> getStats(String code) {

        UrlMapping url = repository.findByShortCode(code)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        return Map.of(
                "originalUrl", url.getOriginalUrl(),
                "clickCount", url.getClickCount(),
                "createdAt", url.getCreatedAt(),
                "expiryDate", url.getExpiryDate()
        );
    }
}