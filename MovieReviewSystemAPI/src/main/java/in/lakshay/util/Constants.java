package in.lakshay.util;

public final class Constants {
    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String API_V1 = "/api/v1";
    public static final String AUTH_PATH = API_V1 + "/auth";
    public static final String REVIEWS_PATH = API_V1 + "/reviews";
    public static final String MOVIES_PATH = API_V1 + "/movies";
    
    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 5;
    public static final int MAX_COMMENT_LENGTH = 1000;
    
    public static final String JWT_TOKEN_VALIDITY = "${jwt.expiration}";
    public static final String JWT_SECRET = "${jwt.secret}";
}
