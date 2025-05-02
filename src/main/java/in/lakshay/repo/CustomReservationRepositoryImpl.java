package in.lakshay.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

// implementation of custom reservation repo interface
// pretty simple for now, just provides entity manager access
// might add more complex query methods later if needed
@Repository
public class CustomReservationRepositoryImpl implements CustomReservationRepository {

    // injected by spring - no need for explicit config
    @PersistenceContext
    private EntityManager entityManager;  // gives us low-level db access

    // just returns the entity manager for now
    // this lets ReservationService do complex queries when needed
    @Override
    public EntityManager getEntityManager() {
        // nothing fancy here yet, just a simple getter
        return entityManager;  // for manual txns and complex queries
    }

    // TODO: add methods for complex reservation operations
    // maybe batch operations or custom reporting queries?
}
