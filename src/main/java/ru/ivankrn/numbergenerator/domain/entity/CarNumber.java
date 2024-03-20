package ru.ivankrn.numbergenerator.domain.entity;

import ru.ivankrn.numbergenerator.domain.exception.InvalidCarNumberException;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class CarNumber {

    private static final Set<Character> validSeriesChars =
            Set.of('А', 'Е', 'Т', 'О', 'Р', 'Н', 'У', 'К', 'Х', 'С', 'В', 'М');

    private int number;
    private String series;
    private Region region;

    private CarNumber(int number, String series, Region region) {
        this.number = number;
        this.series = series;
        this.region = region;
    }

    public static CarNumber createCarNumber(int number, String series, Region region) throws InvalidCarNumberException {
        validateNumber(number);
        validateSeries(series);
        return new CarNumber(number, series, region);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) throws InvalidCarNumberException {
        validateNumber(number);
        this.number = number;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) throws InvalidCarNumberException {
        validateSeries(series);
        this.series = series;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "%c%d%c%c %d RUS".formatted(series.charAt(0), number, series.charAt(1), series.charAt(2), region.code);
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
        return Objects.hash(number, series, region);
    }

    private static void validateNumber(int number) throws InvalidCarNumberException {
        if (number < 0 || number > 999) {
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

}
