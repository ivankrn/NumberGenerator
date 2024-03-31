package ru.ivankrn.numbergenerator.infrastructure.repository;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import ru.ivankrn.numbergenerator.domain.entity.CarNumber;
import ru.ivankrn.numbergenerator.domain.repository.CarNumberRepository;

import java.util.Optional;

@Repository
public class CarNumberRepositoryImpl implements CarNumberRepository {

    // TODO Спросить правильно ли происходит инжект
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
        // TODO Учесть что серия (буквенная часть) следующего номера не обязана быть больше лексиграфически
        // TODO Спросить что следует отдавать если изначально был выдан последний номер
        TypedQuery<CarNumber> query = entityManager
                .createQuery(
                        "SELECT cn FROM CarNumber cn " +
                                "WHERE cn.number > :previous_number OR cn.series > :previous_series"
                        , CarNumber.class);
        query.setParameter("previous_number", previous.getNumber());
        query.setParameter("previous_series", previous.getSeries());
        query.setMaxResults(1);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CarNumber> getByPosition(int position) {
        TypedQuery<CarNumber> query = entityManager
                .createQuery("SELECT n FROM CarNumber n", CarNumber.class);
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
                "INSERT INTO last_issued_car_number(\"number\", series, region) " +
                        "VALUES(:number, :series, :region)"
        );
        insertQuery.setParameter("number", carNumber.getNumber());
        insertQuery.setParameter("series", carNumber.getSeries());
        insertQuery.setParameter("region", carNumber.getRegion());
        insertQuery.executeUpdate();
    }

    @Override
    public void delete(CarNumber carNumber) {
        if (entityManager.contains(carNumber)) {
            entityManager.remove(carNumber);
        } else {
            entityManager.merge(carNumber);
        }
    }

}
