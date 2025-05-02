package in.lakshay.repo;

import in.lakshay.entity.Reservation;
import in.lakshay.entity.Showtime;
import in.lakshay.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

// this is a big one - handles all reservation data access
// lots of filtering options for the admin dashboard
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, CustomReservationRepository {
    // basic user reservation lookups
    List<Reservation> findByUser(User user);

    // filter by payment status
    List<Reservation> findByUserAndPaid(User user, boolean paid);

    // filter by reservation status (confirmed, cancelled, etc)
    List<Reservation> findByUserAndStatusId(User user, Integer statusId);

    // combo filter - both paid status and reservation status
    List<Reservation> findByUserAndPaidAndStatusId(User user, boolean paid, Integer statusId);

    // get all reservations for a showtime
    List<Reservation> findByShowtime(Showtime showtime);

    // This method is replaced by findByUserAndStatusId
    // List<Reservation> findByUserAndStatus(User user, Reservation.ReservationStatus status);

    /**
     * Find upcoming reservations for a user based on the current date and time.
     * A reservation is considered "upcoming" if:
     * 1. The showtime date is in the future, OR
     * 2. The showtime date is today AND the showtime time is later than the current time
     * Only confirmed reservations (statusId = 1) are included.
     */
    @Query("SELECT r FROM Reservation r JOIN r.showtime s WHERE r.user.id = :userId AND (s.showDate > :date OR (s.showDate = :date AND s.showTime >= :time)) AND r.statusId = 1")
    List<Reservation> findUpcomingReservationsByUser(Long userId, LocalDate date, LocalTime time); // for user dashboard

    // admin reporting - get reservations for a specific date
    @Query("SELECT r FROM Reservation r WHERE r.showtime.showDate = :date")
    List<Reservation> findReservationsByDate(LocalDate date);

    // calculate total revenue for a date range - for admin reports
    // only counts confirmed reservations (statusId = 1)
    @Query("SELECT SUM(r.totalPrice) FROM Reservation r WHERE r.statusId = 1 AND r.showtime.showDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueForDateRange(LocalDate startDate, LocalDate endDate); // $$$

    // get all confirmed reservations with pagination
    @Query("SELECT r FROM Reservation r WHERE r.statusId = 1")
    Page<Reservation> findAllConfirmedReservations(Pageable pageable);

    // count reservations made between two dates - for stats
    Long countByReservationTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // same as above but with status filter
    Long countByReservationTimeBetweenAndStatusId(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer statusId);

    // New methods for filtering - admin dashboard stuff below
    // lots of combos for different filter options

    // filter by payment status
    Page<Reservation> findByPaid(boolean paid, Pageable pageable);

    // filter by reservation status
    Page<Reservation> findByStatusId(Integer statusId, Pageable pageable);

    // filter by date range
    Page<Reservation> findByReservationTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    // filter combos - payment + status
    Page<Reservation> findByPaidAndStatusId(boolean paid, Integer statusId, Pageable pageable);

    // filter combos - payment + date range
    Page<Reservation> findByPaidAndReservationTimeBetween(boolean paid, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    // filter combos - status + date range
    Page<Reservation> findByStatusIdAndReservationTimeBetween(Integer statusId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    // the kitchen sink - all filters together
    // this one's a mouthful lol
    Page<Reservation> findByPaidAndStatusIdAndReservationTimeBetween(boolean paid, Integer statusId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
