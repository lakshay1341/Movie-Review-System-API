package in.lakshay.repo;

import jakarta.persistence.EntityManager;

// custom interface for reservation repo
// gives access to entity manager for complex ops
// not much here yet, but might add more custom methods later
public interface CustomReservationRepository {
    // get entity manager for low-level db ops
    // needed for some complex reservation operations
    EntityManager getEntityManager(); // for manual queries/transactions
}
