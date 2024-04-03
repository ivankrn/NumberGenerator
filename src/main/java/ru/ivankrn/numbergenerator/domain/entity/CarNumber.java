package ru.ivankrn.numbergenerator.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import ru.ivankrn.numbergenerator.domain.exception.InvalidCarNumberException;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

@Entity
@Table(name = "car_number")
@SQLDelete(sql = "UPDATE car_number SET is_issued = true WHERE id=?")
@SQLRestriction("is_issued <> true")
public class CarNumber {

    public static final Set<Character> validSeriesChars =
            Set.of('А', 'Е', 'Т', 'О', 'Р', 'Н', 'У', 'К', 'Х', 'С', 'В', 'М');

    @Id
    @SequenceGenerator(name = "car_number_id_seq", sequenceName = "car_number_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_number_id_seq")
    private Long id;
    @Column(nullable = false)
    @NaturalId
    private int number;
    @Column(nullable = false)
    @NaturalId
    private String series;
    @Column(nullable = false)
    @NaturalId
    private Region region;
    @Column(name = "is_issued", nullable = false)
    private boolean isIssued = false;

    private CarNumber() {
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isIssued() {
        return isIssued;
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
        return Objects.hash(number, series, region);
    }

}
