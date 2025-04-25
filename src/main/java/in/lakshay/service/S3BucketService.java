package in.lakshay.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service class for handling operations with AWS S3 bucket for movie posters.
 */
@Service
@Slf4j
public class S3BucketService {

    private final AmazonS3 s3Client;
    private final String bucketName;

    /**
     * Initialize the S3 client using AWS credentials from environment variables.
     */
    public S3BucketService(
            @Value("${aws.access.key.id}") String accessKey,
            @Value("${aws.secret.access.key}") String secretKey,
            @Value("${aws.s3.bucket.name}") String bucketName,
            @Value("${aws.s3.region:us-east-1}") String region) {

        if (accessKey == null || secretKey == null || bucketName == null) {
            log.error("Missing required AWS credentials or bucket name in environment variables");
            throw new IllegalArgumentException("AWS credentials and bucket name must be set in environment variables");
        }

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        this.s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.fromName(region))
                .build();

        this.bucketName = bucketName;

        log.info("S3BucketService initialized with bucket: {}", this.bucketName);
    }

    /**
     * Upload a movie poster to S3 bucket.
     *
     * @param file     The movie poster file to upload
     * @param movieId  The ID of the movie. If not provided, a UUID will be generated.
     * @return Information about the uploaded file including the URL
     */
    public Map<String, Object> uploadPoster(MultipartFile file, Long movieId) throws IOException {
        String fileId = movieId != null ? movieId.toString() : UUID.randomUUID().toString();

        // Extract file extension
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Create a unique file name
        String fileName = "movie_posters/" + fileId + fileExtension;

        // Set metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // Upload to S3
        s3Client.putObject(new PutObjectRequest(
                bucketName,
                fileName,
                file.getInputStream(),
                metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        // Generate the URL for the uploaded file
        String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

        log.info("Successfully uploaded poster for movie_id: {}", fileId);

        Map<String, Object> result = new HashMap<>();
        result.put("movieId", fileId);
        result.put("fileName", fileName);
        result.put("fileUrl", fileUrl);
        result.put("fileSize", file.getSize());
        result.put("contentType", file.getContentType());

        return result;
    }

    /**
     * Generate a pre-signed URL for accessing a movie poster.
     *
     * @param fileName   The name of the file in S3
     * @param expiration URL expiration time in seconds (default: 1 hour)
     * @return Pre-signed URL for accessing the file
     */
    public String getPosterUrl(String fileName, int expiration) {
        // If fileName doesn't start with movie_posters/, add it
        if (!fileName.startsWith("movie_posters/")) {
            fileName = "movie_posters/" + fileName;
        }

        // Set expiration time
        Date expirationDate = new Date();
        expirationDate.setTime(expirationDate.getTime() + expiration * 1000L);

        // Generate pre-signed URL
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
                //.withMethod(HttpMethodName.GET)
                .withMethod((HttpMethod.GET))
                .withExpiration(expirationDate);

        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        log.info("Generated pre-signed URL for {}", fileName);
        return url.toString();
    }

    /**
     * Delete a movie poster from S3 bucket.
     *
     * @param fileName The name of the file to delete
     * @return Status of the delete operation
     */
    public Map<String, String> deletePoster(String fileName) {
        // If fileName doesn't start with movie_posters/, add it
        if (!fileName.startsWith("movie_posters/")) {
            fileName = "movie_posters/" + fileName;
        }

        // Delete the file from S3
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));

        log.info("Successfully deleted poster: {}", fileName);

        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Poster " + fileName + " deleted successfully");

        return result;
    }

    /**
     * List all movie posters in the S3 bucket.
     *
     * @param prefix The prefix to filter objects (default: movie_posters/)
     * @return List of poster objects with their details
     */
    public List<Map<String, Object>> listPosters(String prefix) {
        if (prefix == null) {
            prefix = "movie_posters/";
        }

        // List objects in the bucket with the given prefix
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix);

        ListObjectsV2Result result = s3Client.listObjectsV2(listObjectsRequest);

        // Extract relevant information
        List<Map<String, Object>> posters = new ArrayList<>();
        for (S3ObjectSummary obj : result.getObjectSummaries()) {
            // Generate URL for each poster
            String fileName = obj.getKey();
            String url = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

            Map<String, Object> poster = new HashMap<>();
            poster.put("fileName", fileName);
            poster.put("fileUrl", url);
            poster.put("size", obj.getSize());
            poster.put("lastModified", obj.getLastModified());

            posters.add(poster);
        }

        log.info("Listed {} posters from S3", posters.size());
        return posters;
    }
}
