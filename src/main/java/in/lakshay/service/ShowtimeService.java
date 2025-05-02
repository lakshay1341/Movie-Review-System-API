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

// handles movie showtimes - the core of our reservation system

@Service
@Slf4j // for logging
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository; // showtime data
    private final MovieRepository movieRepository; // movie data
    private final TheaterRepository theaterRepository; // theater data
    private final SeatRepository seatRepository; // seat data
    private final ModelMapper modelMapper; // for dto conversion

    @Autowired // constructor injection
    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository,
                          TheaterRepository theaterRepository, SeatRepository seatRepository,
                          ModelMapper modelMapper) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.seatRepository = seatRepository;
        this.modelMapper = modelMapper;
    }

    // get all showtimes for a specific date
    public List<ShowtimeDTO> getShowtimesByDate(LocalDate date) {
        log.info("Fetching showtimes for date: {}", date);
        return showtimeRepository.findByShowDate(date).stream()
                .map(this::mapToDTO) // convert to DTOs
                .collect(Collectors.toList());
    }

    // find all showtimes for a specific movie
    public List<ShowtimeDTO> getShowtimesByMovie(Long movieId) {
        log.info("Fetching showtimes for movie id: {}", movieId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        return showtimeRepository.findByMovie(movie).stream()
                .map(this::mapToDTO) // entity -> dto
                .collect(Collectors.toList());
    }

    // get all showtimes at a specific theater
    public List<ShowtimeDTO> getShowtimesByTheater(Long theaterId) {
        log.info("Fetching showtimes for theater id: {}", theaterId);
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + theaterId));
        return showtimeRepository.findByTheater(theater).stream()
                .map(this::mapToDTO) // convert to dtos
                .collect(Collectors.toList());
    }

    // get showtimes with available seats from a date (paginated)
    public Page<ShowtimeDTO> getAvailableShowtimes(LocalDate date, Pageable pageable) {
        log.info("Fetching available showtimes from date: {}", date);
        return showtimeRepository.findAvailableShowtimesFromDate(date, pageable)
                .map(this::mapToDTO); // convert to dtos
    }

    // get available showtimes for a specific movie from a date (paginated)
    // this is what the booking page uses
    public Page<ShowtimeDTO> getAvailableShowtimesForMovie(Long movieId, LocalDate date, Pageable pageable) {
        log.info("Fetching available showtimes for movie id: {} from date: {}", movieId, date);
        return showtimeRepository.findAvailableShowtimesForMovieFromDate(movieId, date, pageable)
                .map(this::mapToDTO); // entity -> dto
    }

    // get a single showtime by id
    public ShowtimeDTO getShowtimeById(Long id) {
        log.info("Fetching showtime with id: {}", id);
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));
        return mapToDTO(showtime); // convert to dto
    }

    // create a new showtime with seats
    @Transactional // all or nothing
    public ShowtimeDTO addShowtime(Showtime showtime) {
        // make sure movie exists
        if (showtime.getMovie() == null || showtime.getMovie().getId() == null) {
            throw new IllegalArgumentException("Movie is required for showtime"); // duh
        }

        Movie movie = movieRepository.findById(showtime.getMovie().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showtime.getMovie().getId()));

        // make sure theater exists
        if (showtime.getTheater() == null || showtime.getTheater().getId() == null) {
            throw new IllegalArgumentException("Theater is required for showtime"); // obviously
        }

        Theater theater = theaterRepository.findById(showtime.getTheater().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + showtime.getTheater().getId()));

        log.info("Adding new showtime for movie: {} at theater: {}", movie.getTitle(), theater.getName());

        // set proper references
        showtime.setMovie(movie);
        showtime.setTheater(theater);

        // can't have more seats than the theater has!
        if (showtime.getTotalSeats() > theater.getCapacity()) {
            throw new IllegalArgumentException("Total seats cannot exceed theater capacity of " + theater.getCapacity());
        }

        // all seats available at first
        showtime.setAvailableSeats(showtime.getTotalSeats());

        Showtime savedShowtime = showtimeRepository.save(showtime);
        log.info("Saved showtime with ID: {}", savedShowtime.getId());

        // now create all the individual seats
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= showtime.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setShowtime(savedShowtime);
            // create seat numbers like A1, A2...A9, B1, B2 etc
            seat.setSeatNumber(String.format("%c%d", 'A' + ((i-1) / 10), ((i-1) % 10) + 1));
            seat.setIsReserved(false); // not reserved yet
            seats.add(seat);
        }

        seatRepository.saveAll(seats); // batch insert
        log.info("Created {} seats for showtime ID: {}", seats.size(), savedShowtime.getId());

        return mapToDTO(savedShowtime); // return as dto
    }

    // update an existing showtime
    @Transactional
    public ShowtimeDTO updateShowtime(Long id, Showtime showtimeDetails) {
        log.info("Updating showtime with id: {}", id);
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));

        // check movie if it's changing
        if (showtimeDetails.getMovie() != null && showtimeDetails.getMovie().getId() != null) {
            Movie movie = movieRepository.findById(showtimeDetails.getMovie().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showtimeDetails.getMovie().getId()));
            showtime.setMovie(movie); // update movie reference
        }

        // check theater if it's changing
        if (showtimeDetails.getTheater() != null && showtimeDetails.getTheater().getId() != null) {
            Theater theater = theaterRepository.findById(showtimeDetails.getTheater().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + showtimeDetails.getTheater().getId()));
            showtime.setTheater(theater); // update theater reference
        }

        // update the simple fields
        if (showtimeDetails.getShowDate() != null) {
            showtime.setShowDate(showtimeDetails.getShowDate()); // new date
        }

        if (showtimeDetails.getShowTime() != null) {
            showtime.setShowTime(showtimeDetails.getShowTime()); // new time
        }

        if (showtimeDetails.getPrice() != null) {
            showtime.setPrice(showtimeDetails.getPrice()); // new price
        }

        // DANGER: Don't update total seats or available seats directly - would break reservations!

        Showtime updatedShowtime = showtimeRepository.save(showtime);
        log.info("Updated showtime with ID: {}", updatedShowtime.getId());
        return mapToDTO(updatedShowtime); // convert to dto
    }

    // delete a showtime and its seats
    @Transactional // all or nothing
    public void deleteShowtime(Long id) {
        log.info("Deleting showtime with id: {}", id);
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));

        // safety check - can't delete if people have tickets!
        if (showtime.getReservations() != null && !showtime.getReservations().isEmpty()) {
            throw new IllegalStateException("Cannot delete showtime with existing reservations");
        }

        // gotta delete the seats first (foreign key constraint)
        List<Seat> seats = seatRepository.findByShowtime(showtime);
        if (seats != null && !seats.isEmpty()) {
            log.info("Deleting {} seats for showtime ID: {}", seats.size(), id);
            seatRepository.deleteAll(seats); // bye bye seats
        }

        showtimeRepository.delete(showtime); // now delete the showtime
        log.info("Deleted showtime with ID: {}", id);
    }

    // manual mapping from entity to DTO
    // could use ModelMapper but this is more explicit
    private ShowtimeDTO mapToDTO(Showtime showtime) {
        ShowtimeDTO dto = new ShowtimeDTO();
        dto.setId(showtime.getId());
        dto.setShowDate(showtime.getShowDate()); // date part
        dto.setShowTime(showtime.getShowTime()); // time part
        dto.setTotalSeats(showtime.getTotalSeats()); // total capacity
        dto.setAvailableSeats(showtime.getAvailableSeats()); // remaining seats
        dto.setPrice(showtime.getPrice()); // ticket price

        // include movie details if available
        if (showtime.getMovie() != null) {
            dto.setMovieId(showtime.getMovie().getId());
            dto.setMovieTitle(showtime.getMovie().getTitle());
            dto.setMoviePosterUrl(showtime.getMovie().getPosterImageUrl()); // for display
        }

        // include theater details if available
        if (showtime.getTheater() != null) {
            dto.setTheaterId(showtime.getTheater().getId());
            dto.setTheaterName(showtime.getTheater().getName());
            dto.setTheaterLocation(showtime.getTheater().getLocation()); // address
        }

        return dto; // all done
    }
}
