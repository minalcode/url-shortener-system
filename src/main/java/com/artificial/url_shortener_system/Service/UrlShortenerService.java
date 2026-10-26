package com.artificial.url_shortener_system.Service;

import com.artificial.url_shortener_system.Model.ShortUrl;
import com.artificial.url_shortener_system.Repository.ShortUrlRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class UrlShortenerService {

    private final ShortUrlRepository repository;

    public UrlShortenerService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    // Create a short URL
    public ShortUrl shortenUrl(String originalUrl) {

        if (originalUrl == null || originalUrl.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "URL is required");
        }

        try {
            URI uri = URI.create(originalUrl.trim());

            if (uri.getHost() == null ||
                    !("http".equalsIgnoreCase(uri.getScheme())
                            || "https".equalsIgnoreCase(uri.getScheme()))) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enter a valid HTTP or HTTPS URL");
        }

        String shortCode;

        do {
            shortCode = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 7);
        } while (repository.existsByShortCode(shortCode));

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(originalUrl.trim());
        shortUrl.setShortCode(shortCode);

        return repository.save(shortUrl);
    }

    // Get all saved URLs
    public List<ShortUrl> getAllUrls() {
        return repository.findAll();
    }

    // Find original URL using short code
    public ShortUrl getByShortCode(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Short URL not found"));
    }
}