package in.lakshay.repo;

import in.lakshay.entity.Payment;
import in.lakshay.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservation(Reservation reservation);
    Optional<Payment> findByPaymentIntentId(String paymentIntentId);
}
