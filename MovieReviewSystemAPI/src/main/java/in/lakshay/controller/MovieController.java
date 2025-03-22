package in.lakshay.controller;

import in.lakshay.entity.Movie;
import in.lakshay.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public Page<Movie> getMovies(
            @PageableDefault(page = 0, size = 10, sort = "releaseYear", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String search
    ) {
        log.info("Fetching movies with pageable: {}, search: {}", pageable, search);
        if (search != null && !search.isEmpty()) {
            return movieService.findByTitleOrGenreContainingIgnoreCase(search, pageable);
        } else {
            return movieService.findAllWithReviews(pageable);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        log.info("Adding new movie: {}", movie.getTitle());
        Movie savedMovie = movieService.addMovie(movie);
        return ResponseEntity.ok(savedMovie);
    }
}