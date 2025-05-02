package in.lakshay.exception;

// simple exception for when a resource isn't found
// used for 404 responses
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    // no need for stack trace or anything fancy
}