package in.lakshay.validator;

import org.springframework.stereotype.Component;

// validates password strength
// regex checks for:
// - at least 1 digit
// - at least 1 lowercase
// - at least 1 uppercase
// - at least 1 special char
// - no whitespace
// - min length 8
@Component
public class PasswordValidator {
    // regex is kinda ugly but it works ¯\_(ツ)_/¯
    private static final String PASSWORD_PATTERN =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    // check if password meets our requirements
    public boolean isValid(String password) {
        // null check first, then regex match
        return password != null && password.matches(PASSWORD_PATTERN);
    }
}