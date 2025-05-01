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

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, CustomReservationRepository {
    List<Reservation> findByUser(User user);

    List<Reservation> findByUserAndPaid(User user, boolean paid);

    List<Reservation> findByUserAndStatusId(User user, Integer statusId);

    List<Reservation> findByUserAndPaidAndStatusId(User user, boolean paid, Integer statusId);

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
    List<Reservation> findUpcomingReservationsByUser(Long userId, LocalDate date, LocalTime time);

    @Query("SELECT r FROM Reservation r WHERE r.showtime.showDate = :date")
    List<Reservation> findReservationsByDate(LocalDate date);

    @Query("SELECT SUM(r.totalPrice) FROM Reservation r WHERE r.statusId = 1 AND r.showtime.showDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueForDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Reservation r WHERE r.statusId = 1")
    Page<Reservation> findAllConfirmedReservations(Pageable pageable);

    Long countByReservationTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    Long countByReservationTimeBetweenAndStatusId(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer statusId);

    // New methods for filtering
    Page<Reservation> findByPaid(boolean paid, Pageable pageable);

    Page<Reservation> findByStatusId(Integer statusId, Pageable pageable);

    Page<Reservation> findByReservationTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    Page<Reservation> findByPaidAndStatusId(boolean paid, Integer statusId, Pageable pageable);

    Page<Reservation> findByPaidAndReservationTimeBetween(boolean paid, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    Page<Reservation> findByStatusIdAndReservationTimeBetween(Integer statusId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    Page<Reservation> findByPaidAndStatusIdAndReservationTimeBetween(boolean paid, Integer statusId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
