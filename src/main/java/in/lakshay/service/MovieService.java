package in.lakshay.service;

import in.lakshay.dto.MovieDTO;
import in.lakshay.entity.Movie;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MovieService(MovieRepository movieRepository, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public Page<MovieDTO> findByTitleOrGenreContainingIgnoreCase(String search, Pageable pageable) {
        log.info("Searching for movies with title or genre containing: {}", search);
        return movieRepository.findByTitleOrGenreContainingIgnoreCase(search, pageable)
                .map(movie -> modelMapper.map(movie, MovieDTO.class));
    }

    @Transactional(readOnly = true)
    public Page<MovieDTO> findAllWithReviews(Pageable pageable) {
        log.info("Fetching all movies with reviews");
        return movieRepository.findAllWithReviews(pageable)
                .map(movie -> modelMapper.map(movie, MovieDTO.class));
    }

    @Transactional
    public MovieDTO addMovie(Movie movie) {
        log.info("Adding new movie: {}", movie.getTitle());
        Movie savedMovie = movieRepository.save(movie);
        log.info("Movie saved with ID: {}", savedMovie.getId());
        return modelMapper.map(savedMovie, MovieDTO.class);
    }

    @Transactional(readOnly = true)
    public MovieDTO getMovieById(Long id) {
        log.info("Fetching movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return modelMapper.map(movie, MovieDTO.class);
    }

    @Transactional
    public MovieDTO updateMovie(Long id, Movie movieDetails) {
        log.info("Updating movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        movie.setTitle(movieDetails.getTitle());
        movie.setGenre(movieDetails.getGenre());
        movie.setReleaseYear(movieDetails.getReleaseYear());
        movie.setDescription(movieDetails.getDescription());
        movie.setPosterImageUrl(movieDetails.getPosterImageUrl());

        Movie updatedMovie = movieRepository.save(movie);
        return modelMapper.map(updatedMovie, MovieDTO.class);
    }

    @Transactional
    public void deleteMovie(Long id) {
        log.info("Deleting movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        // Check if movie has any reviews or showtimes before deleting
        if ((movie.getReviews() != null && !movie.getReviews().isEmpty()) ||
            (movie.getShowtimes() != null && !movie.getShowtimes().isEmpty())) {
            throw new RuntimeException("Cannot delete movie with existing reviews or showtimes");
        }

        movieRepository.delete(movie);
        log.info("Movie with id: {} deleted successfully", id);
    }
}