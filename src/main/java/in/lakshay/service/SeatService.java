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

@Service
@Slf4j
public class SeatService {
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SeatService(SeatRepository seatRepository, ShowtimeRepository showtimeRepository, ModelMapper modelMapper) {
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public List<SeatDTO> getSeatsByShowtime(Long showtimeId) {
        log.info("Fetching seats for showtime id: {}", showtimeId);
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + showtimeId));

        List<Seat> seats = seatRepository.findByShowtime(showtime);

        // If no seats found for this showtime, create them automatically
        if (seats.isEmpty()) {
            log.warn("No seats found for showtime id: {}. Creating seats automatically.", showtimeId);
            try {
                seats = createSeatsForShowtime(showtime);
                log.info("Successfully created {} seats for showtime id: {}", seats.size(), showtimeId);
            } catch (Exception e) {
                log.error("Failed to create seats for showtime id: {}, Error: {}", showtimeId, e.getMessage(), e);
                throw new RuntimeException("Failed to create seats for showtime: " + e.getMessage(), e);
            }
        } else {
            log.info("Found {} existing seats for showtime id: {}", seats.size(), showtimeId);
        }

        return seats.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<SeatDTO> getAvailableSeatsByShowtime(Long showtimeId) {
        log.info("Fetching available seats for showtime id: {}", showtimeId);
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + showtimeId));

        // First ensure seats exist for this showtime
        List<Seat> allSeats = seatRepository.findByShowtime(showtime);
        if (allSeats.isEmpty()) {
            log.warn("No seats found for showtime id: {}. Creating seats before fetching available ones.", showtimeId);
            try {
                createSeatsForShowtime(showtime);
            } catch (Exception e) {
                log.error("Failed to create seats for showtime id: {}, Error: {}", showtimeId, e.getMessage(), e);
                throw new RuntimeException("Failed to create seats for showtime: " + e.getMessage(), e);
            }
        }

        List<SeatDTO> availableSeats = seatRepository.findByShowtimeAndIsReserved(showtime, false).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.info("Found {} available seats for showtime id: {}", availableSeats.size(), showtimeId);
        return availableSeats;
    }

    /**
     * Create seats for a showtime based on the theater capacity
     * This method ensures that the correct number of seats are created for each theater type
     */
    @Transactional
    public List<Seat> createSeatsForShowtime(Showtime showtime) {
        if (showtime == null) {
            throw new IllegalArgumentException("Showtime cannot be null");
        }

        if (showtime.getTheater() == null) {
            throw new IllegalArgumentException("Showtime must have an associated theater");
        }

        Long theaterId = showtime.getTheater().getId();
        Integer capacity = showtime.getTheater().getCapacity();

        log.info("Creating seats for showtime id: {} in theater id: {} with capacity: {}",
                showtime.getId(), theaterId, capacity);

        List<Seat> seats = new ArrayList<>();

        // Create seats based on theater type
        if (theaterId == 1) {
            // Cineplex (theater_id = 1) - 150 seats (A-O, 1-10)
            for (char row = 'A'; row <= 'O'; row++) {
                for (int seatNum = 1; seatNum <= 10; seatNum++) {
                    Seat seat = new Seat();
                    seat.setShowtime(showtime);
                    seat.setSeatNumber(row + String.valueOf(seatNum));
                    seat.setIsReserved(false);
                    seats.add(seat);
                }
            }
        } else if (theaterId == 2) {
            // MovieMax (theater_id = 2) - 200 seats (A-J, 1-20)
            for (char row = 'A'; row <= 'J'; row++) {
                for (int seatNum = 1; seatNum <= 20; seatNum++) {
                    Seat seat = new Seat();
                    seat.setShowtime(showtime);
                    seat.setSeatNumber(row + String.valueOf(seatNum));
                    seat.setIsReserved(false);
                    seats.add(seat);
                }
            }
        } else {
            // FilmHouse (theater_id = 3) or any other theater - use capacity to determine seats
            int rows = (int) Math.ceil(Math.sqrt(capacity));
            int seatsPerRow = (int) Math.ceil((double) capacity / rows);

            for (int rowIdx = 0; rowIdx < rows; rowIdx++) {
                char row = (char) ('A' + rowIdx);
                for (int seatNum = 1; seatNum <= seatsPerRow && seats.size() < capacity; seatNum++) {
                    Seat seat = new Seat();
                    seat.setShowtime(showtime);
                    seat.setSeatNumber(row + String.valueOf(seatNum));
                    seat.setIsReserved(false);
                    seats.add(seat);
                }
            }
        }

        // Verify we created the correct number of seats
        if (seats.size() != showtime.getTotalSeats()) {
            log.warn("Created {} seats but showtime has {} total seats. Updating showtime.",
                    seats.size(), showtime.getTotalSeats());
            showtime.setTotalSeats(seats.size());
            showtime.setAvailableSeats(seats.size());
            showtimeRepository.save(showtime);
        }

        log.info("Created {} seats for showtime id: {}", seats.size(), showtime.getId());
        // Save all seats to the database
        return seatRepository.saveAll(seats);
    }

    private SeatDTO mapToDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setId(seat.getId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setIsReserved(seat.getIsReserved());

        if (seat.getShowtime() != null) {
            dto.setShowtimeId(seat.getShowtime().getId());
        }

        return dto;
    }
}
