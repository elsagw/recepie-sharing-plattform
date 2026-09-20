package com.recipenetwork.backend.recipe;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RecipeScrapeService implements RecipeScraper {

    private static final int REQUEST_TIMEOUT_MILLIS = 5000;
    private static final String USER_AGENT = "RecipeNetwork/1.0";
    private static final Pattern JSON_LD_TITLE = Pattern.compile("\\\"name\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
    private static final Pattern JSON_LD_IMAGE = Pattern.compile("\\\"image\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

    private final ExternalRecipeRepository externalRecipeRepository;

    public RecipeScrapeService(ExternalRecipeRepository externalRecipeRepository) {
        this.externalRecipeRepository = externalRecipeRepository;
    }

    @Override
    public ExternalRecipe scrape(String rawUrl) {
        URI uri = normalizeAndValidate(rawUrl);
        String normalizedUrl = uri.toString();

        Optional<ExternalRecipe> cached = externalRecipeRepository.findBySourceUrl(normalizedUrl);
        if (cached.isPresent()) {
            return cached.get();
        }

        try {
            Document document = Jsoup.connect(normalizedUrl)
                    .userAgent(USER_AGENT)
                    .timeout(REQUEST_TIMEOUT_MILLIS)
                    .followRedirects(true)
                    .get();

            String domain = uri.getHost();
            String title = firstNonBlank(
                    metaContent(document, "property", "og:title"),
                    jsonLdValue(document, JSON_LD_TITLE),
                    document.title(),
                    domain);
            String image = firstNonBlank(
                    metaContent(document, "property", "og:image"),
                    jsonLdValue(document, JSON_LD_IMAGE),
                    placeholder(domain));

            return externalRecipeRepository.save(new ExternalRecipe(normalizedUrl, title, image, domain));
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not fetch recipe URL", exception);
        }
    }

    private URI normalizeAndValidate(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL is required");
        }

        try {
            URI uri = new URI(rawUrl.trim()).normalize();
            String scheme = uri.getScheme();
            if (uri.getHost() == null || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL must use HTTP or HTTPS");
            }
            return new URI(scheme.toLowerCase(), uri.getUserInfo(), uri.getHost().toLowerCase(), uri.getPort(),
                    uri.getPath(), uri.getQuery(), null);
        } catch (URISyntaxException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL", exception);
        }
    }

    private String metaContent(Document document, String attribute, String value) {
        return document.select("meta[" + attribute + "=\"" + value + "\"]").attr("content");
    }

    private String jsonLdValue(Document document, Pattern pattern) {
        for (String jsonLd : document.select("script[type=application/ld+json]").eachText()) {
            Matcher matcher = pattern.matcher(jsonLd);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "";
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private String placeholder(String domain) {
        return "https://placehold.co/1200x800?text=" + domain;
    }
}