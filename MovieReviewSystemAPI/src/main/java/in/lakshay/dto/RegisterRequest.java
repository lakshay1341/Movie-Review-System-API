package in.lakshay.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}