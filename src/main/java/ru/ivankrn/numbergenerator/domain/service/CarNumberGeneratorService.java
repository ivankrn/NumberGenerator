package ru.ivankrn.numbergenerator.domain.service;

import ru.ivankrn.numbergenerator.domain.entity.CarNumber;
import ru.ivankrn.numbergenerator.domain.exception.NumbersRanOutException;

public interface CarNumberGeneratorService {

    CarNumber getRandom() throws NumbersRanOutException;
    CarNumber getNext() throws NumbersRanOutException;

}
