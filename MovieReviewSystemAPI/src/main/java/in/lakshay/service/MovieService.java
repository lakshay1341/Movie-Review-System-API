package in.lakshay.service;

import in.lakshay.dto.MovieDTO;
import in.lakshay.entity.Movie;
import in.lakshay.repo.MovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MovieService(MovieRepository movieRepository, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
    }

    public Page<MovieDTO> findByTitleOrGenreContainingIgnoreCase(String search, Pageable pageable) {
        return movieRepository.findByTitleOrGenreContainingIgnoreCase(search, pageable)
                .map(movie -> modelMapper.map(movie, MovieDTO.class));
    }

    public Page<MovieDTO> findAllWithReviews(Pageable pageable) {
        return movieRepository.findAllWithReviews(pageable)
                .map(movie -> modelMapper.map(movie, MovieDTO.class));
    }

    public MovieDTO addMovie(Movie movie) {
        Movie savedMovie = movieRepository.save(movie);
        return modelMapper.map(savedMovie, MovieDTO.class);
    }
}