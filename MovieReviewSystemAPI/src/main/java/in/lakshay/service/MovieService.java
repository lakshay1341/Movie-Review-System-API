package in.lakshay.service;

import in.lakshay.entity.Movie;
import in.lakshay.repo.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public Page<Movie> findByTitleOrGenreContainingIgnoreCase(String search, Pageable pageable) {
        return movieRepository.findByTitleOrGenreContainingIgnoreCase(search, pageable);
    }

    public Page<Movie> findAllWithReviews(Pageable pageable) {
        return movieRepository.findAllWithReviews(pageable);
    }

    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }
}