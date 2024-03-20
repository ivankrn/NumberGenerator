package ru.ivankrn.numbergenerator.domain.repository;

import ru.ivankrn.numbergenerator.domain.entity.CarNumber;

import java.util.Optional;

public interface CarNumberRepository {

    void save(CarNumber carNumber);
    Optional<CarNumber> getLastIssuedNumber();
    Optional<CarNumber> get(String number);
    Optional<CarNumber> getRandom();
    void delete(CarNumber carNumber);

}
