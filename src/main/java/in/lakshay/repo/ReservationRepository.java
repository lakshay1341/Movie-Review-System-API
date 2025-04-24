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

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.showtime.showDate >= :date AND r.statusId = 1")
    List<Reservation> findUpcomingReservationsByUser(Long userId, LocalDate date);

    @Query("SELECT r FROM Reservation r WHERE r.showtime.showDate = :date")
    List<Reservation> findReservationsByDate(LocalDate date);

    @Query("SELECT SUM(r.totalPrice) FROM Reservation r WHERE r.statusId = 1 AND r.showtime.showDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueForDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Reservation r WHERE r.statusId = 1")
    Page<Reservation> findAllConfirmedReservations(Pageable pageable);
}
