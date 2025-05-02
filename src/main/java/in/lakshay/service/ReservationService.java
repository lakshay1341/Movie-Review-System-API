package in.lakshay.service;

import in.lakshay.dto.MasterDataDTO;
import in.lakshay.dto.ReservationDTO;
import in.lakshay.dto.SeatDTO;
import in.lakshay.dto.ShowtimeDTO;
import in.lakshay.entity.Payment;
import in.lakshay.entity.Reservation;
import in.lakshay.entity.Seat;
import in.lakshay.entity.Showtime;
import in.lakshay.entity.User;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.PaymentRepository;
import in.lakshay.repo.ReservationRepository;
import in.lakshay.repo.SeatRepository;
import in.lakshay.repo.ShowtimeRepository;
import in.lakshay.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;
    private final MasterDataService masterDataService;
    private final ModelMapper modelMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository,
                             ShowtimeRepository showtimeRepository, SeatRepository seatRepository,
                             PaymentRepository paymentRepository, MasterDataService masterDataService,
                             ModelMapper modelMapper) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.paymentRepository = paymentRepository;
        this.masterDataService = masterDataService;
        this.modelMapper = modelMapper;
    }

    public List<ReservationDTO> getReservationsByUser(String username) {
        log.info("Fetching reservations for user: {}", username);
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return reservationRepository.findByUser(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> getUpcomingReservationsByUser(String username) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        log.info("Fetching upcoming reservations for user: {} with current date: {} and time: {}", username, currentDate, currentTime);

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        List<Reservation> upcomingReservations = reservationRepository.findUpcomingReservationsByUser(user.getId(), currentDate, currentTime);
        log.info("Found {} upcoming reservations for user: {}", upcomingReservations.size(), username);

        // Log each reservation's details
        upcomingReservations.forEach(reservation -> {
            Showtime showtime = reservation.getShowtime();
            log.info("Reservation ID: {}, Showtime Date: {}, Showtime Time: {}, Movie: {}",
                    reservation.getId(),
                    showtime.getShowDate(),
                    showtime.getShowTime(),
                    showtime.getMovie() != null ? showtime.getMovie().getTitle() : "Unknown");
        });

        return upcomingReservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO getReservationById(Long id) {
        log.info("Fetching reservation with id: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        return mapToDTO(reservation);
    }

    /**
     * Get all reservations with pagination
     * This method was previously named getAllConfirmedReservations but now returns all reservations
     * regardless of status to support the admin dashboard
     *
     * TODO: rename this method in the future to avoid confusion
     */
    public Page<ReservationDTO> getAllConfirmedReservations(Pageable pageable) {
        log.info("Fetching all reservations with pagination");
        // this actually gets ALL reservations, not just confirmed ones! naming is confusing :/
        return reservationRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    public Double calculateRevenueForDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating revenue for date range: {} to {}", startDate, endDate);
        // simple passthrough to repo method
        return reservationRepository.calculateRevenueForDateRange(startDate, endDate);
    }



    // counts ALL reservs in date range
    public Long countReservationsForDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("Counting reservations for date range: {} to {}", startDateTime, endDateTime);
        return reservationRepository.countByReservationTimeBetween(startDateTime, endDateTime);
    }

    // just the confirmed ones (statusId=1)
    public Long countConfirmedReservationsForDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("Counting confirmed reservations for date range: {} to {}", startDateTime, endDateTime);
        return reservationRepository.countByReservationTimeBetweenAndStatusId(startDateTime, endDateTime, 1); // 1 = CONFIRMED
    }

    /*
     * gets canceled ones - statusId 3
     */
    public Long countCanceledReservationsForDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("Counting canceled reservations for date range: {} to {}", startDateTime, endDateTime);
        return reservationRepository.countByReservationTimeBetweenAndStatusId(startDateTime, endDateTime, 3); // 3 = CANCELED
    }

    /**
     * Creates a reservation with CONFIRMED status (pending payment)
     */
    @Transactional
    public ReservationDTO createReservation(String username, Long showtimeId, List<Long> seatIds) {
        log.info("Creating reservation for user: {} for showtime: {} with seats: {}", username, showtimeId, seatIds);

        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (showtimeId == null) {
            throw new IllegalArgumentException("Showtime ID cannot be null");
        }

        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected");
        }

        // Find user
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Find showtime
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + showtimeId));

        // Check if showtime is in the past
        if (showtime.getShowDate().isBefore(LocalDate.now()) ||
            (showtime.getShowDate().isEqual(LocalDate.now()) && showtime.getShowTime().isBefore(LocalTime.now()))) {
            // can't book time machines yet lol
            throw new IllegalStateException("Cannot reserve seats for past showtimes");
        }

        // Check if showtime has available seats
        if (showtime.getAvailableSeats() < seatIds.size()) {
            throw new IllegalStateException("Not enough available seats for this showtime");
        }

        // Get seats with pessimistic lock to prevent concurrent reservations
        // this is super important!! otherwise we could double-book seats
        List<Seat> seats = seatRepository.findByIdInAndShowtimeIdWithLock(seatIds, showtimeId);

        // Validate seats
        if (seats.size() != seatIds.size()) {
            List<Long> foundSeatIds = seats.stream().map(Seat::getId).collect(Collectors.toList());
            List<Long> notFoundSeatIds = seatIds.stream()
                    .filter(id -> !foundSeatIds.contains(id))
                    .collect(Collectors.toList());
            throw new ResourceNotFoundException("Seats not found with IDs: " + notFoundSeatIds);
        }

        // Verify all seats belong to the requested showtime
        List<Seat> wrongShowtimeSeats = seats.stream()
                .filter(seat -> !seat.getShowtime().getId().equals(showtimeId))
                .collect(Collectors.toList());

        if (!wrongShowtimeSeats.isEmpty()) {
            String wrongSeatNumbers = wrongShowtimeSeats.stream()
                    .map(Seat::getSeatNumber)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Seats " + wrongSeatNumbers + " do not belong to the requested showtime");
        }

        // Check if any seat is already reserved
        List<Seat> reservedSeats = seats.stream()
                .filter(Seat::getIsReserved)
                .collect(Collectors.toList());

        if (!reservedSeats.isEmpty()) {
            String reservedSeatNumbers = reservedSeats.stream()
                    .map(Seat::getSeatNumber)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Seats already reserved: " + reservedSeatNumbers);
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setShowtime(showtime);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatusId(1); // 1 = CONFIRMED
        // calc total price based on # of seats
        reservation.setTotalPrice(showtime.getPrice() * seats.size());

        Reservation savedReservation = reservationRepository.save(reservation);

        // Update seats
        seats.forEach(seat -> {
            seat.setIsReserved(true);
            seat.setReservation(savedReservation);
        });

        seatRepository.saveAll(seats);

        // Update available seats count in showtime
        showtime.setAvailableSeats(showtime.getAvailableSeats() - seats.size());
        showtimeRepository.save(showtime);

        return mapToDTO(savedReservation);
    }

    @Transactional
    public ReservationDTO cancelReservation(Long reservationId, String username) {
        log.info("Canceling reservation with id: {} for user: {}", reservationId, username);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        // Check if user owns this reservation
        if (!reservation.getUser().getUserName().equals(username) &&
                !hasRole(username, "ROLE_ADMIN")) {
            throw new RuntimeException("Not authorized to cancel this reservation");
        }

        // Check if showtime is in the past
        if (reservation.getShowtime().getShowDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot cancel reservation for past showtimes");
        }

        // Update reservation status
        reservation.setStatusId(3); // 3 = CANCELED

        // Free up seats
        List<Seat> seats = reservation.getSeats();
        seats.forEach(seat -> {
            seat.setIsReserved(false);
            seat.setReservation(null);
        });

        seatRepository.saveAll(seats);

        // Update available seats count in showtime
        Showtime showtime = reservation.getShowtime();
        showtime.setAvailableSeats(showtime.getAvailableSeats() + seats.size());
        showtimeRepository.save(showtime);

        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToDTO(updatedReservation);
    }

    // quick helper to check if user has specific role
    private boolean hasRole(String username, String roleName) {
        return userRepository.findByUserName(username)
                .map(user -> user.getRole().getName().equals(roleName))
                .orElse(false);
    }


    // bunch of filter methods below...

    /**
     * Get reservations for a user filtered by payment status
     */
    public List<ReservationDTO> getReservationsByUserAndPaymentStatus(String username, boolean paid) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        List<Reservation> reservations = reservationRepository.findByUserAndPaid(user, paid);
        return reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get reservations for a user filtered by status ID
     */
    public List<ReservationDTO> getReservationsByUserAndStatusId(String username, Integer statusId) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        List<Reservation> reservations = reservationRepository.findByUserAndStatusId(user, statusId);
        return reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get reservations for a user filtered by payment status and status ID
     */
    public List<ReservationDTO> getReservationsByUserAndPaymentStatusAndStatusId(String username, boolean paid, Integer statusId) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        List<Reservation> reservations = reservationRepository.findByUserAndPaidAndStatusId(user, paid, statusId);
        return reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReservationDTO mapToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setStatusId(reservation.getStatusId());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setPaid(reservation.isPaid());

        // Get status value from master data
        try {
            MasterDataDTO statusData = masterDataService.getMasterDataByComponentTypeNameAndMasterDataId("RESERVATION_STATUS", reservation.getStatusId());
            dto.setStatusValue(statusData.getValue());
        } catch (Exception e) {
            log.warn("Could not find status value for status ID: {}, Error: {}", reservation.getStatusId(), e.getMessage());
            // Set a default status value based on the status ID
            // hardcoded fallback in case masterdata service fails
            switch (reservation.getStatusId()) {
                case 1:
                    dto.setStatusValue("CONFIRMED");
                    break;
                case 2:
                    dto.setStatusValue("PAID");
                    break;
                case 3:
                    dto.setStatusValue("CANCELED");
                    break;
                default:
                    dto.setStatusValue("Unknown"); // shouldn't happen but who knows
            }
        }

        if (reservation.getUser() != null) {
            dto.setUserName(reservation.getUser().getUserName());
        }

        if (reservation.getShowtime() != null) {
            Showtime showtime = reservation.getShowtime();
            dto.setShowtimeId(showtime.getId());

            if (showtime.getMovie() != null) {
                dto.setMovieTitle(showtime.getMovie().getTitle());
            }

            if (showtime.getTheater() != null) {
                dto.setTheaterName(showtime.getTheater().getName());
            }

            dto.setShowDate(showtime.getShowDate().format(DATE_FORMATTER));
            dto.setShowTime(showtime.getShowTime().format(TIME_FORMATTER));

            // Create and set ShowtimeDTO
            ShowtimeDTO showtimeDTO = new ShowtimeDTO();
            showtimeDTO.setId(showtime.getId());
            showtimeDTO.setShowDate(showtime.getShowDate()); // date obj
            showtimeDTO.setShowTime(showtime.getShowTime()); // time obj
            showtimeDTO.setTotalSeats(showtime.getTotalSeats());
            showtimeDTO.setAvailableSeats(showtime.getAvailableSeats());
            showtimeDTO.setPrice(showtime.getPrice()); // in $

            // Include movie and theater data if available
            if (showtime.getMovie() != null) {
                showtimeDTO.setMovieId(showtime.getMovie().getId());
                showtimeDTO.setMovieTitle(showtime.getMovie().getTitle());
                showtimeDTO.setMoviePosterUrl(showtime.getMovie().getPosterImageUrl());
            }

            if (showtime.getTheater() != null) {
                showtimeDTO.setTheaterId(showtime.getTheater().getId());
                showtimeDTO.setTheaterName(showtime.getTheater().getName());
                showtimeDTO.setTheaterLocation(showtime.getTheater().getLocation());
            }

            dto.setShowtime(showtimeDTO);
        }

        List<SeatDTO> seatDTOs = reservation.getSeats().stream()
                .map(seat -> {
                    SeatDTO seatDTO = new SeatDTO();
                    seatDTO.setId(seat.getId());
                    seatDTO.setSeatNumber(seat.getSeatNumber());
                    seatDTO.setIsReserved(seat.getIsReserved());
                    if (seat.getShowtime() != null) {
                        seatDTO.setShowtimeId(seat.getShowtime().getId());
                    }
                    return seatDTO;
                })
                .collect(Collectors.toList());

        dto.setSeats(seatDTOs);

        return dto;
    }
}
