package in.lakshay.repo;

import in.lakshay.entity.Payment;
import in.lakshay.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// handles payment data - pretty simple for now
// might need more methods as payment features expand
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // find payment by reservation
    Optional<Payment> findByReservation(Reservation reservation); // used to check if paid

    // find by stripe payment intent id
    Optional<Payment> findByPaymentIntentId(String paymentIntentId); // for webhook handling
}
