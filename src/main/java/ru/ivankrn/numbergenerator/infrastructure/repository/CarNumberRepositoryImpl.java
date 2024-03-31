package ru.ivankrn.numbergenerator.infrastructure.repository;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import ru.ivankrn.numbergenerator.domain.entity.CarNumber;
import ru.ivankrn.numbergenerator.domain.repository.CarNumberRepository;

import java.util.Optional;

// TODO Спросить, какие есть конвенции Liquibase, и как его чаще используют в промышленной разработке
@Repository
public class CarNumberRepositoryImpl implements CarNumberRepository {

    // TODO Спросить правильно ли происходит инжект (данная аннотация должна инжектить entitymanager, управляемый
    //  контейнером, из-за чего как я понял спринг должен котроллировать транзакции. Вместе с тем раз я сам определил
    //  бины datasource и entitymanagerfactory для hibernate, то неизвестно, может ли спринг ими управлять или нет)
    // TODO Спросить можно ли было использовать Spring Data JPA
    // TODO Спросить про то как на настоящих проектах контроллируют транзакции. Варианты:
    //  1) spring transaction management
    //  2) управление транзакциями напрямую (например, вручную получая и закрывая entitymanager / session). Если так,
    //  то сессии нужно получать в сервисах и передавать в репозитории, чтобы одной бизнес-операции соответствовала
    //  одна транзакция?
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
        // TODO Спросить как следовало лучше разделить доменную область, раз в репозитории оказались
        //  sql специфичные методы
        TypedQuery<CarNumber> query = entityManager
                .createQuery("SELECT n FROM CarNumber n", CarNumber.class);
        query.setFirstResult(position);
        // TODO Спросить почему offset ниже ограничен int'ом, если строк в БД может быть гораздо больше
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
        // TODO Спросить нельзя ли сделать покрасивее хранение последнего выданного номера, без использования
        //  native запросов. В теории можно было бы создать отдельную сущность LastIssuedCarNumber, чтобы hibernate
        //  сохранял её в отдельную таблицу, но это выглядит как костыль и к тому же скорее всего противоречит домену.
        // TODO Также спросить, можно ли прикрутить maven плагин liquibase, который на основе сущностей сам генерирует
        //  changelog'и. Ведь в данном случае он не "увидит" сущность последнего созданного номера и не
        //  создаст / изменит для неё таблицы
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
