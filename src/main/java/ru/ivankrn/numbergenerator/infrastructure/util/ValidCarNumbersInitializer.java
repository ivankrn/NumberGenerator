package ru.ivankrn.numbergenerator.infrastructure.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ivankrn.numbergenerator.domain.entity.CarNumber;
import ru.ivankrn.numbergenerator.domain.entity.Region;
import ru.ivankrn.numbergenerator.domain.repository.CarNumberRepository;

import java.util.Iterator;
import java.util.List;

@Component
public class ValidCarNumbersInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ValidCarNumbersInitializer.class);
    private final CarNumberRepository carNumberRepository;

    public ValidCarNumbersInitializer(CarNumberRepository carNumberRepository) {
        this.carNumberRepository = carNumberRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (carNumberRepository.getCount() != 0) {
            logger.info("Car numbers database isn't empty, skipping valid car numbers initialization.");
            return;
        }
        logger.info("Valid car numbers initialization started.");
        List<Character> sortedValidChars = CarNumber.validSeriesChars.stream().sorted().toList();
        Iterator<CarNumber> carNumberIterator = new CarNumberIterator(
                sortedValidChars,
                0,
                999,
                Region.TATARSTAN
        );
        while (carNumberIterator.hasNext()) {
            carNumberRepository.save(carNumberIterator.next());
        }
        logger.info("Valid car numbers initialization completed.");
    }

}
