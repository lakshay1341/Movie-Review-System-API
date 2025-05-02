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

// handles all the S3 bucket operations for movie posters
// using constructor injection here unlike other controllers
@RestController
@RequestMapping("/api/v1/posters") // base path for poster endpoints
@Slf4j // logging
public class S3BucketController {

    private final S3BucketService s3BucketService; // s3 operations

    @Autowired
    public S3BucketController(S3BucketService s3BucketService) {
        this.s3BucketService = s3BucketService;
    }

    // upload a movie poster to S3 bucket
    // returns info about the uploaded file including the URL
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    public ResponseEntity<?> uploadPoster(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "movieId", required = false) Long movieId) {

        try {
            log.info("Uploading poster for movie ID: {}", movieId);

            if (file.isEmpty()) {
                // can't upload an empty file
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "poster.upload.empty", null));
            }

            // upload to s3 and get result with url
            Map<String, Object> result = s3BucketService.uploadPoster(file, movieId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "poster.upload.success", result));

        } catch (IOException e) {
            // something went wrong with the upload
            log.error("Error uploading poster: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "poster.upload.error", e.getMessage()));
        }
    }

    // get a pre-signed URL for accessing a movie poster
    // needed for temporary access to private s3 objects
    @GetMapping("/url")
    public ResponseEntity<?> getPosterUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "expiration", defaultValue = "3600") int expiration) {

        try {
            log.info("Generating pre-signed URL for file: {}", fileName);

            // get url that expires after specified time
            String url = s3BucketService.getPosterUrl(fileName, expiration);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "poster.url.success", url));

        } catch (Exception e) {
            // something went wrong generating the url
            log.error("Error generating pre-signed URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "poster.url.error", e.getMessage()));
        }
    }

    // delete a movie poster from S3 bucket
    // be careful with this - it's permanent!
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    public ResponseEntity<?> deletePoster(@RequestParam("fileName") String fileName) {
        try {
            log.info("Deleting poster: {}", fileName);

            // delete from s3 and get result
            Map<String, String> result = s3BucketService.deletePoster(fileName);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "poster.delete.success", result));

        } catch (Exception e) {
            // something went wrong with the deletion
            log.error("Error deleting poster: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "poster.delete.error", e.getMessage()));
        }
    }

    // list all movie posters in the S3 bucket
    // useful for admin dashboard to see all uploaded posters
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    public ResponseEntity<?> listPosters(
            @RequestParam(value = "prefix", defaultValue = "movie_posters/") String prefix) {

        try {
            log.info("Listing posters with prefix: {}", prefix);

            // get all posters with the given prefix
            List<Map<String, Object>> posters = s3BucketService.listPosters(prefix);
            log.info("Found {} posters", posters.size()); // helpful for debugging

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "poster.list.success", posters));

        } catch (Exception e) {
            // something went wrong listing the posters
            log.error("Error listing posters: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "poster.list.error", e.getMessage()));
        }
    }
} // end of S3BucketController
