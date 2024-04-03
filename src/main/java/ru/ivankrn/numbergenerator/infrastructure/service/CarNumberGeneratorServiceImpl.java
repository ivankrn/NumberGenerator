package ru.ivankrn.numbergenerator.infrastructure.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.ivankrn.numbergenerator.domain.entity.CarNumber;
import ru.ivankrn.numbergenerator.domain.exception.NumbersRanOutException;
import ru.ivankrn.numbergenerator.domain.repository.CarNumberRepository;
import ru.ivankrn.numbergenerator.domain.service.CarNumberGeneratorService;

import java.util.Optional;
import java.util.Random;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class CarNumberGeneratorServiceImpl implements CarNumberGeneratorService {

    private final CarNumberRepository carNumberRepository;

    public CarNumberGeneratorServiceImpl(CarNumberRepository carNumberRepository) {
        this.carNumberRepository = carNumberRepository;
    }

    @Override
    public CarNumber getRandom() throws NumbersRanOutException {
        long totalCarNumberCount = carNumberRepository.getCount();
        if (totalCarNumberCount != 0) {
            long randomOffset = new Random().nextLong(0, totalCarNumberCount);
            CarNumber randomCarNumber = carNumberRepository.getByPosition((int) randomOffset)
                    .orElseThrow(NumbersRanOutException::new);
            carNumberRepository.issue(randomCarNumber);
            carNumberRepository.setLastIssuedNumber(randomCarNumber);
            return randomCarNumber;
        }
        throw new NumbersRanOutException();
    }

    @Override
    public CarNumber getNext() throws NumbersRanOutException {
        Optional<CarNumber> lastIssuedCarNumber = carNumberRepository.getLastIssuedNumber();
        Optional<CarNumber> nextCarNumber;
        if (lastIssuedCarNumber.isPresent()) {
            nextCarNumber = carNumberRepository.getNext(lastIssuedCarNumber.get());
        } else {
            Optional<CarNumber> firstCarNumber = carNumberRepository.getByPosition(0);
            nextCarNumber = firstCarNumber;
        }
        if (nextCarNumber.isPresent()) {
            carNumberRepository.issue(nextCarNumber.get());
            carNumberRepository.setLastIssuedNumber(nextCarNumber.get());
            return nextCarNumber.get();
        }
        throw new NumbersRanOutException();
    }

}
