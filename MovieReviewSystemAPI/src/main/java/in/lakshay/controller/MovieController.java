package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.MovieDTO;
import in.lakshay.entity.Movie;
import in.lakshay.service.MovieService;
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

@RestController
@RequestMapping("/movies")
@Slf4j
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<?> getMovies(
            @PageableDefault(page = 0, size = 10, sort = "releaseYear", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String search) {

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
    public ResponseEntity<?> addMovie(@RequestBody Movie movie) {
        MovieDTO savedMovie = movieService.addMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                true,
                "movie.created.success",
                savedMovie
        ));
    }
}