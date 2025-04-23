package in.lakshay.validator;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    public boolean isValid(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }
}