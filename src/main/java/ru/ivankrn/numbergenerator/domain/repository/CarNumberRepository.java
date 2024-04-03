package ru.ivankrn.numbergenerator.domain.repository;

import ru.ivankrn.numbergenerator.domain.entity.CarNumber;

import java.util.Optional;

public interface CarNumberRepository {

    void save(CarNumber carNumber);
    Optional<CarNumber> getNext(CarNumber previous);
    Optional<CarNumber> getByPosition(int position);
    long getCount();
    Optional<CarNumber> getLastIssuedNumber();
    void setLastIssuedNumber(CarNumber carNumber);
    void issue(CarNumber carNumber);

}
