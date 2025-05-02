package in.lakshay.dto;

import lombok.*;

// standard api response wrapper
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String status;  // "success" or "error"
    private String message;  // success/error msg
    private T data;  // payload (can be null)

    // Constructor that accepts boolean success and converts to string status
    public ApiResponse(boolean success, String message, T data) {
        this.status = success ? "success" : "error";
        this.message = message;
        this.data = data;
    }

    // used for all REST endpoints for consistent format
}