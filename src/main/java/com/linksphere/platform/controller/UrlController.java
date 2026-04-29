package com.linksphere.platform.controller;

import com.linksphere.platform.DTO.UrlRequest;
import com.linksphere.platform.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UrlController {

    @Autowired
    private UrlService service;

    // ✅ CREATE SHORT URL (with expiry support)
    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody UrlRequest request) {

        if (request.getUrl() == null || request.getUrl().isBlank()) {
            return ResponseEntity.badRequest().body("URL cannot be empty");
        }

        String shortCode = service.shortenUrl(request);

        return ResponseEntity.ok("http://localhost:8080/" + shortCode);
    }

    // ✅ REDIRECT (with click count + expiry handled in service)
    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code) {

        try {
            String originalUrl = service.getOriginalUrlAndTrack(code);

            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();

        } catch (RuntimeException ex) {

            // 🔥 Expired link
            if (ex.getMessage().equals("EXPIRED")) {
                return ResponseEntity.status(HttpStatus.GONE)
                        .body("This link has expired");
            }

            // 🔥 Not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Short URL not found");
        }
    }

    // ✅ STATS API (analytics)
    @GetMapping("/stats/{code}")
    public ResponseEntity<?> stats(@PathVariable String code) {
        return ResponseEntity.ok(service.getStats(code));
    }
}