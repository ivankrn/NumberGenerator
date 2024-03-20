package ru.ivankrn.numbergenerator.domain.service;

import ru.ivankrn.numbergenerator.domain.entity.CarNumber;
import ru.ivankrn.numbergenerator.domain.exception.NumbersAreOverException;

public interface CarNumberGeneratorService {

    CarNumber getRandom() throws NumbersAreOverException;
    CarNumber getNext() throws NumbersAreOverException;

}
