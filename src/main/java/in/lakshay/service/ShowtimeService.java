package in.lakshay.service;

import in.lakshay.dto.ShowtimeDTO;
import in.lakshay.entity.Movie;
import in.lakshay.entity.Seat;
import in.lakshay.entity.Showtime;
import in.lakshay.entity.Theater;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.MovieRepository;
import in.lakshay.repo.SeatRepository;
import in.lakshay.repo.ShowtimeRepository;
import in.lakshay.repo.TheaterRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final SeatRepository seatRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository,
                          TheaterRepository theaterRepository, SeatRepository seatRepository,
                          ModelMapper modelMapper) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.seatRepository = seatRepository;
        this.modelMapper = modelMapper;
    }

    public List<ShowtimeDTO> getShowtimesByDate(LocalDate date) {
        log.info("Fetching showtimes for date: {}", date);
        return showtimeRepository.findByShowDate(date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDTO> getShowtimesByMovie(Long movieId) {
        log.info("Fetching showtimes for movie id: {}", movieId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        return showtimeRepository.findByMovie(movie).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDTO> getShowtimesByTheater(Long theaterId) {
        log.info("Fetching showtimes for theater id: {}", theaterId);
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + theaterId));
        return showtimeRepository.findByTheater(theater).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Page<ShowtimeDTO> getAvailableShowtimes(LocalDate date, Pageable pageable) {
        log.info("Fetching available showtimes from date: {}", date);
        return showtimeRepository.findAvailableShowtimesFromDate(date, pageable)
                .map(this::mapToDTO);
    }

    public Page<ShowtimeDTO> getAvailableShowtimesForMovie(Long movieId, LocalDate date, Pageable pageable) {
        log.info("Fetching available showtimes for movie id: {} from date: {}", movieId, date);
        return showtimeRepository.findAvailableShowtimesForMovieFromDate(movieId, date, pageable)
                .map(this::mapToDTO);
    }

    public ShowtimeDTO getShowtimeById(Long id) {
        log.info("Fetching showtime with id: {}", id);
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));
        return mapToDTO(showtime);
    }

    @Transactional
    public ShowtimeDTO addShowtime(Showtime showtime) {
        // Validate movie exists
        if (showtime.getMovie() == null || showtime.getMovie().getId() == null) {
            throw new IllegalArgumentException("Movie is required for showtime");
        }

        Movie movie = movieRepository.findById(showtime.getMovie().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showtime.getMovie().getId()));

        // Validate theater exists
        if (showtime.getTheater() == null || showtime.getTheater().getId() == null) {
            throw new IllegalArgumentException("Theater is required for showtime");
        }

        Theater theater = theaterRepository.findById(showtime.getTheater().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + showtime.getTheater().getId()));

        log.info("Adding new showtime for movie: {} at theater: {}", movie.getTitle(), theater.getName());

        // Set movie and theater references
        showtime.setMovie(movie);
        showtime.setTheater(theater);

        // Validate total seats doesn't exceed theater capacity
        if (showtime.getTotalSeats() > theater.getCapacity()) {
            throw new IllegalArgumentException("Total seats cannot exceed theater capacity of " + theater.getCapacity());
        }

        // Set available seats equal to total seats initially
        showtime.setAvailableSeats(showtime.getTotalSeats());

        Showtime savedShowtime = showtimeRepository.save(showtime);
        log.info("Saved showtime with ID: {}", savedShowtime.getId());

        // Create individual seats for this showtime
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= showtime.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setShowtime(savedShowtime);
            seat.setSeatNumber(String.format("%c%d", 'A' + ((i-1) / 10), ((i-1) % 10) + 1));
            seat.setIsReserved(false);
            seats.add(seat);
        }

        seatRepository.saveAll(seats);
        log.info("Created {} seats for showtime ID: {}", seats.size(), savedShowtime.getId());

        return mapToDTO(savedShowtime);
    }

    @Transactional
    public ShowtimeDTO updateShowtime(Long id, Showtime showtimeDetails) {
        log.info("Updating showtime with id: {}", id);
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));

        // Validate movie if it's being updated
        if (showtimeDetails.getMovie() != null && showtimeDetails.getMovie().getId() != null) {
            Movie movie = movieRepository.findById(showtimeDetails.getMovie().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showtimeDetails.getMovie().getId()));
            showtime.setMovie(movie);
        }

        // Validate theater if it's being updated
        if (showtimeDetails.getTheater() != null && showtimeDetails.getTheater().getId() != null) {
            Theater theater = theaterRepository.findById(showtimeDetails.getTheater().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + showtimeDetails.getTheater().getId()));
            showtime.setTheater(theater);
        }

        // Update basic properties
        if (showtimeDetails.getShowDate() != null) {
            showtime.setShowDate(showtimeDetails.getShowDate());
        }

        if (showtimeDetails.getShowTime() != null) {
            showtime.setShowTime(showtimeDetails.getShowTime());
        }

        if (showtimeDetails.getPrice() != null) {
            showtime.setPrice(showtimeDetails.getPrice());
        }

        // Don't update total seats or available seats directly as it could affect existing reservations

        Showtime updatedShowtime = showtimeRepository.save(showtime);
        log.info("Updated showtime with ID: {}", updatedShowtime.getId());
        return mapToDTO(updatedShowtime);
    }

    @Transactional
    public void deleteShowtime(Long id) {
        log.info("Deleting showtime with id: {}", id);
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));

        // Check if there are any reservations for this showtime
        if (showtime.getReservations() != null && !showtime.getReservations().isEmpty()) {
            throw new IllegalStateException("Cannot delete showtime with existing reservations");
        }

        // Delete associated seats first
        List<Seat> seats = seatRepository.findByShowtime(showtime);
        if (seats != null && !seats.isEmpty()) {
            log.info("Deleting {} seats for showtime ID: {}", seats.size(), id);
            seatRepository.deleteAll(seats);
        }

        showtimeRepository.delete(showtime);
        log.info("Deleted showtime with ID: {}", id);
    }

    private ShowtimeDTO mapToDTO(Showtime showtime) {
        ShowtimeDTO dto = new ShowtimeDTO();
        dto.setId(showtime.getId());
        dto.setShowDate(showtime.getShowDate());
        dto.setShowTime(showtime.getShowTime());
        dto.setTotalSeats(showtime.getTotalSeats());
        dto.setAvailableSeats(showtime.getAvailableSeats());
        dto.setPrice(showtime.getPrice());

        if (showtime.getMovie() != null) {
            dto.setMovieId(showtime.getMovie().getId());
            dto.setMovieTitle(showtime.getMovie().getTitle());
            dto.setMoviePosterUrl(showtime.getMovie().getPosterImageUrl());
        }

        if (showtime.getTheater() != null) {
            dto.setTheaterId(showtime.getTheater().getId());
            dto.setTheaterName(showtime.getTheater().getName());
            dto.setTheaterLocation(showtime.getTheater().getLocation());
        }

        return dto;
    }
}
