package ru.ivankrn.numbergenerator.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ivankrn.numbergenerator.domain.exception.NumbersRanOutException;
import ru.ivankrn.numbergenerator.domain.service.CarNumberGeneratorService;

@RestController
@RequestMapping("/number")
public class NumberController {

    private final CarNumberGeneratorService carNumberGeneratorService;

    public NumberController(CarNumberGeneratorService carNumberGeneratorService) {
        this.carNumberGeneratorService = carNumberGeneratorService;
    }

    @GetMapping("/random")
    public String getRandom() throws NumbersRanOutException {
        return carNumberGeneratorService.getRandom().toString();
    }

    @GetMapping("/next")
    public String getNext() throws NumbersRanOutException {
        return carNumberGeneratorService.getNext().toString();
    }

}
