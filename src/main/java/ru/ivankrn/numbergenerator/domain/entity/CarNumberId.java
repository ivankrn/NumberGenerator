package ru.ivankrn.numbergenerator.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class CarNumberId implements Serializable {

    private int number;
    private String series;

    private Region region;

    private CarNumberId() {}

    public CarNumberId(int number, String series, Region region) {
        this.number = number;
        this.series = series;
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarNumberId that = (CarNumberId) o;
        return number == that.number && Objects.equals(series, that.series) && region == that.region;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, series, region);
    }

}
