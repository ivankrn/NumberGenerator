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
        // TODO Спросить как не допустить "утечки" бизнес-правила валидности номера из домена в техническую область.
        //  В текущем варианте алгоритм по сути "захардкожен", ведь я передаю в итератор список валидных символов и
        //  минимальный / максимальный номер для генерации.
        logger.info("Valid car numbers initialization started.");
        List<Character> sortedValidChars = CarNumber.validSeriesChars.stream().sorted().toList();
        Iterator<CarNumber> carNumberIterator = new CarNumberIterator(
                sortedValidChars,
                0,
                999,
                Region.TATARSTAN
        );
        // TODO Спросить можно ли ускорить вставку. На текущий момент на моем устройстве генерация 1,7 млн. номеров
        //  занимает 2,5 минуты, и последующая вставка в базу около 40 секунд (даже с учетом batch вставки и
        //  использования одной транзакции).
        while (carNumberIterator.hasNext()) {
            carNumberRepository.save(carNumberIterator.next());
        }
        // TODO Спросить как учесть что вставка в БД на этот момент ещё может не произойти
        logger.info("Valid car numbers initialization completed.");
        // TODO Спросить как следовало бы реализовать вставку при условии работы нескольких инстансов приложения.
        //  Запускать один инстанс, ожидать пока он завершит вставку, после чего подтягивать остальные?
    }

}
