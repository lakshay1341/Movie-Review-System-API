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

// handles all the movie stuff - CRUD ops, search, etc

@Service
@Slf4j // logging ftw
public class MovieService {
    private final MovieRepository movieRepository; // db stuff
    private final ModelMapper modelMapper; // entity/dto mapper thingy

    @Autowired // constructor injection ftw
    public MovieService(MovieRepository movieRepository, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
    }

    // search by title/genre - ignores case cuz users don't care about caps
    @Transactional(readOnly = true) // no writes here
    public Page<MovieDTO> findByTitleOrGenreContainingIgnoreCase(String search, Pageable pageable) {
        log.info("Searching for movies with title or genre containing: {}", search);
        return movieRepository.findByTitleOrGenreContainingIgnoreCase(search, pageable)
                .map(movie -> modelMapper.map(movie, MovieDTO.class)); // convert to DTOs
    }

    // the big filter method - handles all search params
    @Transactional(readOnly = true)
    public Page<MovieDTO> findMoviesWithFilters(String search, String genre, Integer releaseYear, Pageable pageable) {
        log.info("Filtering movies with search: {}, genre: {}, releaseYear: {}", search, genre, releaseYear);
        // this query is kinda slow with lots of data but works for now
        return movieRepository.findMoviesWithFilters(search, genre, releaseYear, pageable)
                .map(movie -> modelMapper.map(movie, MovieDTO.class));
    }

    // gets all movies + their reviews in one go
    @Transactional(readOnly = true)
    public Page<MovieDTO> findAllWithReviews(Pageable pageable) {
        log.info("Fetching all movies with reviews");
        return movieRepository.findAllWithReviews(pageable)
                .map(movie -> modelMapper.map(movie, MovieDTO.class));
    }


    // create new movie
    @Transactional
    public MovieDTO addMovie(Movie movie) {
        log.info("Adding new movie: {}", movie.getTitle());
        Movie savedMovie = movieRepository.save(movie); // do the db insert
        log.info("Movie saved with ID: {}", savedMovie.getId());
        return modelMapper.map(savedMovie, MovieDTO.class); // convert back to dto
    }

    // get single movie by id
    @Transactional(readOnly = true)
    public MovieDTO getMovieById(Long id) {
        log.info("Fetching movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return modelMapper.map(movie, MovieDTO.class);
    }

    // update existing movie
    @Transactional
    public MovieDTO updateMovie(Long id, Movie movieDetails) {
        log.info("Updating movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        // update all fields - maybe should use BeanUtils.copyProperties? meh this works
        movie.setTitle(movieDetails.getTitle());
        movie.setGenre(movieDetails.getGenre());
        movie.setReleaseYear(movieDetails.getReleaseYear());
        movie.setDescription(movieDetails.getDescription());
        movie.setPosterImageUrl(movieDetails.getPosterImageUrl()); // might be null

        Movie updatedMovie = movieRepository.save(movie);
        return modelMapper.map(updatedMovie, MovieDTO.class); // back to dto
    }

    // delete a movie if it has no dependencies
    @Transactional
    public void deleteMovie(Long id) {
        log.info("Deleting movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        // Check if movie has any reviews or showtimes before deleting
        // TODO: maybe we should just cascade delete instead of throwing error?
        //       would be easier but might delete stuff users care about
        if ((movie.getReviews() != null && !movie.getReviews().isEmpty()) ||
            (movie.getShowtimes() != null && !movie.getShowtimes().isEmpty())) {
            throw new RuntimeException("Cannot delete movie with existing reviews or showtimes");
        }

        movieRepository.delete(movie); // bye bye movie
        log.info("Movie with id: {} deleted successfully", id);
    }
}