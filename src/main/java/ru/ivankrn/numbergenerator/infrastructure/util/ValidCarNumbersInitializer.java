package ru.ivankrn.numbergenerator.infrastructure.util;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.ivankrn.numbergenerator.domain.entity.CarNumber;
import ru.ivankrn.numbergenerator.domain.entity.Region;
import ru.ivankrn.numbergenerator.domain.repository.CarNumberRepository;

import java.util.Iterator;
import java.util.List;

@Component
public class ValidCarNumbersInitializer implements CommandLineRunner {

    private final CarNumberRepository carNumberRepository;
    private static final Logger logger = LoggerFactory.getLogger(ValidCarNumbersInitializer.class);

    public ValidCarNumbersInitializer(CarNumberRepository carNumberRepository) {
        this.carNumberRepository = carNumberRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (carNumberRepository.getCount() != 0) {
            logger.info("Database isn't empty, skipping valid car numbers initialization.");
            return;
        }
        logger.info("Valid car numbers initialization started.");
        Iterator<CarNumber> carNumberIterator = new CarNumberIterator(
                List.of('В', 'А'),
                0,
                1,
                Region.TATARSTAN
        );
        while (carNumberIterator.hasNext()) {
            carNumberRepository.save(carNumberIterator.next());
        }
        // TODO Учесть что вставка в БД на этот момент ещё может не произойти
        logger.info("Valid car numbers initialization completed.");
    }

}
