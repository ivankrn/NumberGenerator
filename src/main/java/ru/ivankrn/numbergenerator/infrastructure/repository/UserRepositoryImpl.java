package ru.ivankrn.numbergenerator.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.ivankrn.numbergenerator.domain.entity.User;
import ru.ivankrn.numbergenerator.domain.repository.UserRepository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public UserRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(User user) {
        entityManager.merge(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        TypedQuery<User> query =
                entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public long getCount() {
        return entityManager
                .createQuery("SELECT COUNT(*) FROM User", Long.class)
                .getSingleResult();
    }

    @Override
    public void delete(User user) {
        if (entityManager.contains(user)) {
            entityManager.remove(user);
        } else {
            entityManager.merge(user);
        }
    }

}
