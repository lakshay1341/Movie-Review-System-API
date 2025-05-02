package in.lakshay.service;

import in.lakshay.dto.SeatDTO;
import in.lakshay.entity.Seat;
import in.lakshay.entity.Showtime;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.SeatRepository;
import in.lakshay.repo.ShowtimeRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// handles theater seats - creation, reservation status, etc

@Service
@Slf4j // logging
public class SeatService {
    private final SeatRepository seatRepository; // seat data access
    private final ShowtimeRepository showtimeRepository; // showtime data access
    private final ModelMapper modelMapper; // for dto conversion

    @Autowired // constructor injection
    public SeatService(SeatRepository seatRepository, ShowtimeRepository showtimeRepository, ModelMapper modelMapper) {
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
        this.modelMapper = modelMapper;
    }

    // get all seats for a showtime - creates them if they don't exist
    @Transactional
    public List<SeatDTO> getSeatsByShowtime(Long showtimeId) {
        log.info("Fetching seats for showtime id: {}", showtimeId);
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + showtimeId));

        log.info("Found showtime: {} for movie: {} at theater: {}",
                showtimeId,
                showtime.getMovie() != null ? showtime.getMovie().getTitle() : "unknown",
                showtime.getTheater() != null ? showtime.getTheater().getName() : "unknown");

        List<Seat> seats = seatRepository.findByShowtime(showtime);

        // If no seats found for this showtime, create them automatically
        if (seats == null || seats.isEmpty()) {
            log.warn("No seats found for showtime id: {}. Creating seats automatically.", showtimeId);
            try {
                seats = createSeatsForShowtime(showtime); // create missing seats
                log.info("Successfully created {} seats for showtime id: {}", seats.size(), showtimeId);
            } catch (Exception e) {
                log.error("Failed to create seats for showtime id: {}, Error: {}", showtimeId, e.getMessage(), e);
                throw new RuntimeException("Failed to create seats for showtime: " + e.getMessage(), e); // bubble up
            }
        } else {
            log.info("Found {} existing seats for showtime id: {}", seats.size(), showtimeId);
        }

        List<SeatDTO> seatDTOs = seats.stream()
                .map(this::mapToDTO) // convert to DTOs
                .collect(Collectors.toList());

        log.info("Returning {} seat DTOs for showtime id: {}", seatDTOs.size(), showtimeId);
        return seatDTOs;
    }

    // get only available (unreserved) seats for a showtime
    @Transactional
    public List<SeatDTO> getAvailableSeatsByShowtime(Long showtimeId) {
        log.info("Fetching available seats for showtime id: {}", showtimeId);
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + showtimeId));

        // make sure seats exist first
        List<Seat> allSeats = seatRepository.findByShowtime(showtime);
        if (allSeats.isEmpty()) {
            log.warn("No seats found for showtime id: {}. Creating seats before fetching available ones.", showtimeId);
            try {
                createSeatsForShowtime(showtime); // create the missing seats
            } catch (Exception e) {
                log.error("Failed to create seats for showtime id: {}, Error: {}", showtimeId, e.getMessage(), e);
                throw new RuntimeException("Failed to create seats for showtime: " + e.getMessage(), e); // rethrow
            }
        }

        // now get only the unreserved seats
        List<SeatDTO> availableSeats = seatRepository.findByShowtimeAndIsReserved(showtime, false).stream()
                .map(this::mapToDTO) // convert to DTOs
                .collect(Collectors.toList());

        log.info("Found {} available seats for showtime id: {}", availableSeats.size(), showtimeId);
        return availableSeats; // these are the seats users can book
    }

    // creates all the seats for a showtime based on theater layout
    // different theaters have different seating arrangements
    @Transactional
    public List<Seat> createSeatsForShowtime(Showtime showtime) {
        if (showtime == null) {
            throw new IllegalArgumentException("Showtime cannot be null"); // duh
        }

        if (showtime.getTheater() == null) {
            throw new IllegalArgumentException("Showtime must have an associated theater"); // obviously
        }

        Long theaterId = showtime.getTheater().getId();
        Integer capacity = showtime.getTheater().getCapacity();

        log.info("Creating seats for showtime id: {} in theater id: {} with capacity: {}",
                showtime.getId(), theaterId, capacity);

        List<Seat> seats = new ArrayList<>();

        // each theater has a different layout
        if (theaterId == 1) {
            // Cineplex (theater_id = 1) - 150 seats (A-O, 1-10)
            log.info("Creating seats for Cineplex theater layout (15 rows x 10 seats)");
            for (char row = 'A'; row <= 'O'; row++) { // 15 rows
                for (int seatNum = 1; seatNum <= 10; seatNum++) { // 10 seats per row
                    Seat seat = new Seat();
                    seat.setShowtime(showtime);
                    seat.setSeatNumber(row + String.valueOf(seatNum)); // like A1, B5, etc
                    seat.setIsReserved(false); // all available at first
                    seats.add(seat);
                }
            }
        } else if (theaterId == 2) {
            // MovieMax (theater_id = 2) - 200 seats (A-J, 1-20)
            log.info("Creating seats for MovieMax theater layout (10 rows x 20 seats)");
            for (char row = 'A'; row <= 'J'; row++) { // 10 rows
                for (int seatNum = 1; seatNum <= 20; seatNum++) { // 20 seats per row
                    Seat seat = new Seat();
                    seat.setShowtime(showtime);
                    seat.setSeatNumber(row + String.valueOf(seatNum));
                    seat.setIsReserved(false); // not reserved
                    seats.add(seat);
                }
            }
        } else {
            // FilmHouse (theater_id = 3) or any other theater - calculate layout
            // try to make it roughly square-ish
            int rows = (int) Math.ceil(Math.sqrt(capacity)); // square root for # of rows
            int seatsPerRow = (int) Math.ceil((double) capacity / rows); // seats per row
            log.info("Creating seats for theater id: {} with dynamic layout ({} rows x {} seats)",
                    theaterId, rows, seatsPerRow);

            for (int rowIdx = 0; rowIdx < rows; rowIdx++) {
                char row = (char) ('A' + rowIdx); // A, B, C, etc.
                for (int seatNum = 1; seatNum <= seatsPerRow && seats.size() < capacity; seatNum++) {
                    Seat seat = new Seat();
                    seat.setShowtime(showtime);
                    seat.setSeatNumber(row + String.valueOf(seatNum));
                    seat.setIsReserved(false); // available
                    seats.add(seat);
                }
            }
        }

        // make sure the showtime's seat count matches what we created
        // sometimes the counts can get out of sync
        if (seats.size() != showtime.getTotalSeats()) {
            log.warn("Created {} seats but showtime has {} total seats. Updating showtime.",
                    seats.size(), showtime.getTotalSeats());
            showtime.setTotalSeats(seats.size()); // fix the total
            showtime.setAvailableSeats(seats.size()); // fix available too
            showtimeRepository.save(showtime); // update the showtime
        }

        log.info("Created {} seats for showtime id: {}, saving to database...", seats.size(), showtime.getId());
        // save all the seats in one batch
        List<Seat> savedSeats = seatRepository.saveAll(seats); // return the saved seats
        log.info("Successfully saved {} seats to database for showtime id: {}", savedSeats.size(), showtime.getId());
        return savedSeats;
    }

    // convert seat entity to DTO
    // manual mapping is simpler than configuring ModelMapper for this
    private SeatDTO mapToDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setId(seat.getId());
        dto.setSeatNumber(seat.getSeatNumber()); // like A1, B5, etc
        dto.setIsReserved(seat.getIsReserved()); // reservation status

        if (seat.getShowtime() != null) {
            dto.setShowtimeId(seat.getShowtime().getId()); // parent showtime
        }

        return dto; // all done
    }
}
