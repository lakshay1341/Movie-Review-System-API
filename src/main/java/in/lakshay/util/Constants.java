package in.lakshay.util;

// constants used throughout the app
// keep everything in 1 place to avoid magic strings/numbers
public final class Constants {
    // prevent instantiation
    private Constants() {
        throw new IllegalStateException("Utility class"); // sonar likes this
    }

    // API endpoints
    public static final String API_V1 = "/api/v1"; // base path
    public static final String AUTH_PATH = API_V1 + "/auth"; // login/register
    public static final String REVIEWS_PATH = API_V1 + "/reviews"; // movie reviews
    public static final String MOVIES_PATH = API_V1 + "/movies"; // movie crud
    public static final String THEATERS_PATH = API_V1 + "/theaters"; // theater crud

    // more endpoints
    public static final String SHOWTIMES_PATH = API_V1 + "/showtimes"; // showtime crud
    public static final String SEATS_PATH = API_V1 + "/seats"; // seat mgmt
    public static final String RESERVATIONS_PATH = API_V1 + "/reservations"; // booking stuff

    // review constraints - star rating system
    public static final int MIN_RATING = 1; // min stars
    public static final int MAX_RATING = 5; // max stars
    public static final int MAX_COMMENT_LENGTH = 1000; // prevent huge reviews

    // jwt config - values injected from props
    // TODO: check if we need to rotate these regularly
    public static final String JWT_TOKEN_VALIDITY = "${jwt.expiration}"; // in ms
    public static final String JWT_SECRET = "${jwt.secret}"; // base64 encoded
}
