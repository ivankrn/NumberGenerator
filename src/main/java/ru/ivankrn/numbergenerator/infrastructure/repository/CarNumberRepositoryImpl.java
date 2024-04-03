package ru.ivankrn.numbergenerator.infrastructure.repository;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import ru.ivankrn.numbergenerator.domain.entity.CarNumber;
import ru.ivankrn.numbergenerator.domain.repository.CarNumberRepository;

import java.util.Optional;

@Repository
public class CarNumberRepositoryImpl implements CarNumberRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public CarNumberRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(CarNumber carNumber) {
        entityManager.merge(carNumber);
    }

    @Override
    public Optional<CarNumber> getNext(CarNumber previous) {
        TypedQuery<CarNumber> nextCarNumberQuery = entityManager
                .createQuery(
                        "SELECT cn FROM CarNumber cn " +
                                "WHERE cn.id > :id " +
                                "ORDER BY id"
                        , CarNumber.class);
        nextCarNumberQuery.setParameter("id", previous.getId());
        nextCarNumberQuery.setMaxResults(1);
        try {
            return Optional.of(nextCarNumberQuery.getSingleResult());
        } catch (NoResultException e) {
            Optional<CarNumber> firstCarNumber = getByPosition(0);
            return firstCarNumber;
        }
    }

    @Override
    public Optional<CarNumber> getByPosition(int position) {
        TypedQuery<CarNumber> query = entityManager
                .createQuery("SELECT n FROM CarNumber n ORDER BY id", CarNumber.class);
        query.setFirstResult(position);
        query.setMaxResults(1);
        try {
            CarNumber carNumber = query.getSingleResult();
            return Optional.of(carNumber);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public long getCount() {
        return entityManager
                .createQuery("SELECT COUNT(*) FROM CarNumber", Long.class)
                .getSingleResult();
    }

    @Override
    public Optional<CarNumber> getLastIssuedNumber() {
        Query query = entityManager.createNativeQuery("SELECT * FROM last_issued_car_number", CarNumber.class);
        try {
            return Optional.of((CarNumber) query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void setLastIssuedNumber(CarNumber carNumber) {
        Query truncateQuery = entityManager.createNativeQuery("TRUNCATE last_issued_car_number");
        truncateQuery.executeUpdate();
        Query insertQuery = entityManager.createNativeQuery(
                "INSERT INTO last_issued_car_number(id, \"number\", series, region, is_issued) " +
                        "VALUES(:id, :number, :series, :region, :is_issued)"
        );
        insertQuery.setParameter("id", carNumber.getId());
        insertQuery.setParameter("number", carNumber.getNumber());
        insertQuery.setParameter("series", carNumber.getSeries());
        insertQuery.setParameter("region", carNumber.getRegion());
        insertQuery.setParameter("is_issued", carNumber.isIssued());
        insertQuery.executeUpdate();
    }

    @Override
    public void issue(CarNumber carNumber) {
        if (entityManager.contains(carNumber)) {
            entityManager.remove(carNumber);
        } else {
            entityManager.merge(carNumber);
        }
    }

}
