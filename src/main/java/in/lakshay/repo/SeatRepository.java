package in.lakshay.repo;

import in.lakshay.entity.Seat;
import in.lakshay.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;

// handles seat data access
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    // get all seats for a showtime
    List<Seat> findByShowtime(Showtime showtime);

    // find seats by reservation status for a showtime
    List<Seat> findByShowtimeAndIsReserved(Showtime showtime, Boolean isReserved);

    // important! uses db lock to prevent double-booking
    // this is critical for concurrent reservation attempts
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id IN :seatIds AND s.showtime.id = :showtimeId")
    List<Seat> findByIdInAndShowtimeIdWithLock(List<Long> seatIds, Long showtimeId); // prevents race conditions
}
