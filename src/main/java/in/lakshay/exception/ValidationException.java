package in.lakshay.exception;

import java.util.List;

// thrown when validation fails on user input
// holds a list of validation errors to return to client
public class ValidationException extends RuntimeException {
    private final List<String> errors; // all the validation errors

    public ValidationException(List<String> errors) {
        super("Validation failed"); // generic msg
        this.errors = errors;
    }

    // getter for the errors list
    public List<String> getErrors() {
        return errors; // todo: maybe return a copy instead?
    }
}