package com.artificial.url_shortener_system.Controller;

import com.artificial.url_shortener_system.Model.ShortUrl;
import com.artificial.url_shortener_system.Service.UrlShortenerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    // Create short URL
    @PostMapping("/shorten")
    public Map<String, Object> shortenUrl(
            @RequestBody Map<String, String> request) {

        ShortUrl savedUrl =
                service.shortenUrl(request.get("originalUrl"));

        return Map.of(
                "id", savedUrl.getId(),
                "originalUrl", savedUrl.getOriginalUrl(),
                "shortCode", savedUrl.getShortCode(),
                "shortUrl", "http://localhost:8080/api/urls/"
                        + savedUrl.getShortCode(),
                "createdAt", savedUrl.getCreatedAt()
        );
    }

    // Get all URLs
    @GetMapping
    public List<ShortUrl> getAllUrls() {
        return service.getAllUrls();
    }

    // Redirect short URL to original URL
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectUrl(
            @PathVariable String shortCode) {

        ShortUrl url = service.getByShortCode(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, url.getOriginalUrl())
                .build();
    }
}