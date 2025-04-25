package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.service.S3BucketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling S3 bucket operations for movie posters.
 */
@RestController
@RequestMapping("/api/v1/posters")
@Slf4j
public class S3BucketController {

    private final S3BucketService s3BucketService;

    @Autowired
    public S3BucketController(S3BucketService s3BucketService) {
        this.s3BucketService = s3BucketService;
    }

    /**
     * Upload a movie poster to S3 bucket.
     *
     * @param file    The movie poster file to upload
     * @param movieId The ID of the movie (optional)
     * @return Information about the uploaded file including the URL
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadPoster(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "movieId", required = false) Long movieId) {
        
        try {
            log.info("Uploading poster for movie ID: {}", movieId);
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "poster.upload.empty", null));
            }
            
            Map<String, Object> result = s3BucketService.uploadPoster(file, movieId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "poster.upload.success", result));
            
        } catch (IOException e) {
            log.error("Error uploading poster: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "poster.upload.error", e.getMessage()));
        }
    }

    /**
     * Get a pre-signed URL for accessing a movie poster.
     *
     * @param fileName   The name of the file in S3
     * @param expiration URL expiration time in seconds (default: 3600)
     * @return Pre-signed URL for accessing the file
     */
    @GetMapping("/url")
    public ResponseEntity<?> getPosterUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "expiration", defaultValue = "3600") int expiration) {
        
        try {
            log.info("Generating pre-signed URL for file: {}", fileName);
            
            String url = s3BucketService.getPosterUrl(fileName, expiration);
            
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "poster.url.success", url));
            
        } catch (Exception e) {
            log.error("Error generating pre-signed URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "poster.url.error", e.getMessage()));
        }
    }

    /**
     * Delete a movie poster from S3 bucket.
     *
     * @param fileName The name of the file to delete
     * @return Status of the delete operation
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deletePoster(@RequestParam("fileName") String fileName) {
        try {
            log.info("Deleting poster: {}", fileName);
            
            Map<String, String> result = s3BucketService.deletePoster(fileName);
            
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "poster.delete.success", result));
            
        } catch (Exception e) {
            log.error("Error deleting poster: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "poster.delete.error", e.getMessage()));
        }
    }

    /**
     * List all movie posters in the S3 bucket.
     *
     * @param prefix The prefix to filter objects (default: movie_posters/)
     * @return List of poster objects with their details
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> listPosters(
            @RequestParam(value = "prefix", defaultValue = "movie_posters/") String prefix) {
        
        try {
            log.info("Listing posters with prefix: {}", prefix);
            
            List<Map<String, Object>> posters = s3BucketService.listPosters(prefix);
            
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "poster.list.success", posters));
            
        } catch (Exception e) {
            log.error("Error listing posters: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "poster.list.error", e.getMessage()));
        }
    }
}
