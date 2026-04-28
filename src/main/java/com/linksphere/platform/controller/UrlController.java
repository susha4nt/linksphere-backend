package com.linksphere.platform.controller;

import com.linksphere.platform.DTO.UrlRequest;
import com.linksphere.platform.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UrlController {

    @Autowired
    private UrlService service;



    @PostMapping("/shorten")
    public String shorten(@RequestBody UrlRequest request) {

        String cleanUrl = request.getUrl().trim();

        return "http://localhost:8080/" + service.shortenUrl(cleanUrl);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = service.getOriginalUrl(code);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
