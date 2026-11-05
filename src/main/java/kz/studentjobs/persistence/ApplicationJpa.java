package kz.studentjobs.persistence;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ApplicationJpa {

    private final EntityManager em;

    public ApplicationJpa(EntityManager em) {
        this.em = em;
    }

    public void insert(ApplicationEntity application) {
        em.persist(application);
    }

    public Optional<ApplicationEntity> findById(UUID id) {
        return Optional.ofNullable(
                em.find(ApplicationEntity.class, id)
        );
    }

    public int count(String businessKey) {
        Long result = em.createQuery(
                        """
                        select count(a)
                        from ApplicationEntity a
                        where a.businessKey = :businessKey
                        """,
                        Long.class
                )
                .setParameter("businessKey", businessKey)
                .getSingleResult();

        return result.intValue();
    }

    public void flush() {
        em.flush();
    }
}