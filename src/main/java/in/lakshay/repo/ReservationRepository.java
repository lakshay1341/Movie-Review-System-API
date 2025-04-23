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
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    
    List<Reservation> findByShowtime(Showtime showtime);
    
    List<Reservation> findByUserAndStatus(User user, Reservation.ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.showtime.showDate >= :date")
    List<Reservation> findUpcomingReservationsByUser(Long userId, LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.showtime.showDate = :date")
    List<Reservation> findReservationsByDate(LocalDate date);
    
    @Query("SELECT SUM(r.totalPrice) FROM Reservation r WHERE r.status = 'CONFIRMED' AND r.showtime.showDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueForDateRange(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.status = 'CONFIRMED'")
    Page<Reservation> findAllConfirmedReservations(Pageable pageable);
}
