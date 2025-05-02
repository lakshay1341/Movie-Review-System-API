package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.ShowtimeDTO;
import in.lakshay.dto.ShowtimeRequest;
import in.lakshay.entity.Movie;
import in.lakshay.entity.Showtime;
import in.lakshay.entity.Theater;
import in.lakshay.repo.MovieRepository;
import in.lakshay.repo.TheaterRepository;
import in.lakshay.service.ShowtimeService;
import in.lakshay.util.Constants;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(Constants.SHOWTIMES_PATH) // /api/v1/showtimes
@Slf4j // for logging
@Tag(name = "Showtimes", description = "Showtime management APIs") // swagger docs
public class ShowtimeController {
    @Autowired // TODO: switch to constructor injection
    private ShowtimeService showtimeService; // handles business logic

    @Autowired
    private MovieRepository movieRepository; // for movie lookups

    @Autowired
    private TheaterRepository theaterRepository; // for theater lookups

    @Autowired
    private MessageSource messageSource; // i18n

    @RateLimiter(name = "basic") // prevent abuse
    @GetMapping
    @Operation(summary = "Get showtimes by date", description = "Returns showtimes for a specific date")
    public ResponseEntity<ApiResponse<List<ShowtimeDTO>>> getShowtimesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Fetching showtimes for date: {}", date);
        List<ShowtimeDTO> showtimes = showtimeService.getShowtimesByDate(date);

        // return the showtimes for the requested date
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("showtimes.retrieved.success", null, LocaleContextHolder.getLocale()),
                showtimes
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/movies/{movieId}")
    @Operation(summary = "Get showtimes by movie", description = "Returns showtimes for a specific movie")
    public ResponseEntity<ApiResponse<List<ShowtimeDTO>>> getShowtimesByMovie(@PathVariable Long movieId) {
        log.info("Fetching showtimes for movie id: {}", movieId);
        List<ShowtimeDTO> showtimes = showtimeService.getShowtimesByMovie(movieId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("showtimes.retrieved.success", null, LocaleContextHolder.getLocale()),
                showtimes
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/theaters/{theaterId}")
    @Operation(summary = "Get showtimes by theater", description = "Returns showtimes for a specific theater")
    public ResponseEntity<ApiResponse<List<ShowtimeDTO>>> getShowtimesByTheater(@PathVariable Long theaterId) {
        log.info("Fetching showtimes for theater id: {}", theaterId);
        List<ShowtimeDTO> showtimes = showtimeService.getShowtimesByTheater(theaterId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("showtimes.retrieved.success", null, LocaleContextHolder.getLocale()),
                showtimes
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/available")
    @Operation(summary = "Get available showtimes", description = "Returns available showtimes from a specific date")
    public ResponseEntity<ApiResponse<Page<ShowtimeDTO>>> getAvailableShowtimes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 10) Pageable pageable) {
        // default to today if no date provided
        LocalDate searchDate = date != null ? date : LocalDate.now();
        log.info("Fetching available showtimes from date: {}", searchDate);

        // get showtimes with available seats
        Page<ShowtimeDTO> showtimes = showtimeService.getAvailableShowtimes(searchDate, pageable);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("showtimes.retrieved.success", null, LocaleContextHolder.getLocale()),
                showtimes
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/available/movies/{movieId}") // filter by movie
    @Operation(summary = "Get available showtimes for a movie", description = "Returns available showtimes for a specific movie from a date")
    public ResponseEntity<ApiResponse<Page<ShowtimeDTO>>> getAvailableShowtimesForMovie(
            @PathVariable Long movieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 10) Pageable pageable) {
        // default to today if no date provided
        LocalDate searchDate = date != null ? date : LocalDate.now();
        log.info("Fetching available showtimes for movie id: {} from date: {}", movieId, searchDate);

        // get showtimes for this movie with available seats
        Page<ShowtimeDTO> showtimes = showtimeService.getAvailableShowtimesForMovie(movieId, searchDate, pageable);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("showtimes.retrieved.success", null, LocaleContextHolder.getLocale()),
                showtimes
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/{id}")
    @Operation(summary = "Get showtime by ID", description = "Returns a showtime by its ID")
    public ResponseEntity<ApiResponse<ShowtimeDTO>> getShowtimeById(@PathVariable Long id) {
        log.info("Fetching showtime with id: {}", id);
        ShowtimeDTO showtime = showtimeService.getShowtimeById(id);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("showtime.retrieved.success", null, LocaleContextHolder.getLocale()),
                showtime
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    @Operation(summary = "Add a new showtime", description = "Creates a new showtime (Admin only)")
    public ResponseEntity<ApiResponse<ShowtimeDTO>> addShowtime(@Valid @RequestBody ShowtimeRequest showtimeRequest) {
        log.info("Adding new showtime for movie id: {} at theater id: {}",
                showtimeRequest.getMovieId(), showtimeRequest.getTheaterId());

        // find the movie - will throw exception if not found
        Movie movie = movieRepository.findById(showtimeRequest.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + showtimeRequest.getMovieId()));

        // find the theater - will throw exception if not found
        Theater theater = theaterRepository.findById(showtimeRequest.getTheaterId())
                .orElseThrow(() -> new RuntimeException("Theater not found with id: " + showtimeRequest.getTheaterId()));

        // create new showtime obj
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setTheater(theater);
        showtime.setShowDate(showtimeRequest.getShowDate());
        showtime.setShowTime(showtimeRequest.getShowTime());
        showtime.setTotalSeats(showtimeRequest.getTotalSeats()); // should match theater capacity usually
        showtime.setPrice(showtimeRequest.getPrice());

        // save it and generate seats
        ShowtimeDTO savedShowtime = showtimeService.addShowtime(showtime);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        messageSource.getMessage("showtime.created.success", null, LocaleContextHolder.getLocale()),
                        savedShowtime
                ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    @Operation(summary = "Update a showtime", description = "Updates an existing showtime (Admin only)")
    public ResponseEntity<ApiResponse<ShowtimeDTO>> updateShowtime(
            @PathVariable Long id,
            @Valid @RequestBody ShowtimeRequest showtimeRequest) {
        log.info("Updating showtime with id: {}", id);

        // note: we only update date, time and price
        // can't change movie, theater or seats once created
        // would need to delete and recreate instead
        Showtime showtime = new Showtime();
        showtime.setShowDate(showtimeRequest.getShowDate());
        showtime.setShowTime(showtimeRequest.getShowTime());
        showtime.setPrice(showtimeRequest.getPrice());

        ShowtimeDTO updatedShowtime = showtimeService.updateShowtime(id, showtime);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("showtime.updated.success", null, LocaleContextHolder.getLocale()),
                updatedShowtime
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    @Operation(summary = "Delete a showtime", description = "Deletes an existing showtime (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteShowtime(@PathVariable Long id) {
        log.info("Deleting showtime with id: {}", id);
        // this will cascade delete seats and check for existing reservations
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("showtime.deleted.success", null, LocaleContextHolder.getLocale()),
                null
        ));
    }
} // end of ShowtimeController
