package in.lakshay.repo;

import jakarta.persistence.EntityManager;

public interface CustomReservationRepository {
    EntityManager getEntityManager();
}
