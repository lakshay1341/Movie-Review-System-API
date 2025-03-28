package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.MovieDTO;
import in.lakshay.dto.MovieRequest;
import in.lakshay.entity.Movie;
import in.lakshay.exception.ValidationException;
import in.lakshay.service.MovieService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RestController
@RequestMapping("/api/v1/movies")
@Slf4j
public class MovieController {
    @Autowired
    private MovieService movieService;
    
    @Autowired
    private MessageSource messageSource;

    @RateLimiter(name = "basic")
    @GetMapping
    public ResponseEntity<?> getMovies(
            @PageableDefault(page = 0, size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String search) {
        log.info("Fetching movies with pageable: {}, search: {}", pageable, search);

        Page<MovieDTO> movies = (search != null && !search.isEmpty())
                ? movieService.findByTitleOrGenreContainingIgnoreCase(search, pageable)
                : movieService.findAllWithReviews(pageable);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "movie.retrieved.success",
                movies
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addMovie(@Valid @RequestBody MovieRequest movieRequest) {
        try {
            Movie movie = new Movie();
            movie.setTitle(movieRequest.getTitle());
            movie.setGenre(movieRequest.getGenre());
            movie.setReleaseYear(movieRequest.getReleaseYear());
            
            MovieDTO savedMovie = movieService.addMovie(movie);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            messageSource.getMessage("""
                                movie.created.success""".trim(), 
                                null,
                                LocaleContextHolder.getLocale()
                            ),
                            savedMovie
                    ));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            false,
                            messageSource.getMessage(
                                "validation.failed",
                                null,
                                LocaleContextHolder.getLocale()
                            ),
                            e.getErrors()
                    ));
        }
    }
}