package ru.ivankrn.numbergenerator.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import ru.ivankrn.numbergenerator.domain.exception.InvalidCarNumberException;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

@Entity
@Table(name = "car_number")
@IdClass(CarNumberId.class)
public class CarNumber {

    public static final Set<Character> validSeriesChars =
            Set.of('А', 'Е', 'Т', 'О', 'Р', 'Н', 'У', 'К', 'Х', 'С', 'В', 'М');

    // TODO Спросить про натуральные и суррогатные ключи (может лучше сделать отдельный простой
    //  автоинкрементирующийся суррогатный ключ, а не использовать составной ключ?). Не забыть про аннотацию @NaturalId
    // TODO Спросить, влияют ли композитные ключи на производительность БД и если да, то как сильно. Спросить
    //  применяются ли они на практике, или предпочтение отдается простым ключам.
    @Id
    private int number;
    @Id
    private String series;
    @Id
    private Region region;

    private CarNumber() {
    }

    private CarNumber(int number, String series, Region region) {
        this.number = number;
        this.series = series;
        this.region = region;
    }

    public static CarNumber createCarNumber(int number, String series, Region region) throws InvalidCarNumberException {
        // TODO Спросить, возможно стоит просто передавать строки в фабрику? Хоть текущий вариант и отражает доменную
        //  область, но может быть, было бы проще поступить иначе?

        // TODO Спросить как следует производить валидацию, чтобы её логика не дублировалась в нескольких местах.
        //  Варианты:
        //  1) создать что-то вроде CarNumberValidator, и при валидации в фабрике / сущности делегировать валидацию
        //  валидатору?
        //  2) буквенную и численную часть номера обернуть в дополнительные value object'ы и встроить валидацию в них?
        validateNumber(number);
        validateSeries(series);
        return new CarNumber(number, series, region);
    }

    private static void validateNumber(int number) throws InvalidCarNumberException {
        if (number < 0 || number > 999) {
            // TODO Спросить действительно ли лучше делать подобные строки константами, или JVM достаточно умна
            //  чтобы определить их как неизменяемые и поместить в пул строк
            throw new InvalidCarNumberException("Number should be between 0 and 999");
        }
    }

    private static void validateSeries(String series) throws InvalidCarNumberException {
        if (series.length() != 3) {
            throw new InvalidCarNumberException("Series must be 3 characters long");
        }
        if (series.chars().mapToObj(c -> (char) c).anyMatch(Predicate.not(validSeriesChars::contains))) {
            throw new InvalidCarNumberException("Series must consist only of: " + validSeriesChars);
        }
    }

    public int getNumber() {
        return number;
    }

    public String getSeries() {
        return series;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return "%c%03d%c%c %d RUS".formatted(series.charAt(0), number, series.charAt(1), series.charAt(2), region.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarNumber carNumber = (CarNumber) o;
        return number == carNumber.number && Objects.equals(series, carNumber.series) && region == carNumber.region;
    }

    @Override
    public int hashCode() {
        // TODO Спросить про идентичность в домене и Hibernate
        //  (equals и hashcode переопределяются специфично для Hibernate)
        return Objects.hash(number, series, region);
    }

}
