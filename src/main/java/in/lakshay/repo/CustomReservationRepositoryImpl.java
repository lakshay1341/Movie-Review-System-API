package in.lakshay.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CustomReservationRepositoryImpl implements CustomReservationRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
