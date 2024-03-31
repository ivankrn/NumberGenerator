package ru.ivankrn.numbergenerator.infrastructure.util;

import com.google.common.base.Joiner;
import ru.ivankrn.numbergenerator.domain.entity.CarNumber;
import ru.ivankrn.numbergenerator.domain.entity.Region;
import ru.ivankrn.numbergenerator.domain.exception.InvalidCarNumberException;

import java.util.*;

public class CarNumberIterator implements Iterator<CarNumber> {

    private final List<Character> validChars;
    private final Map<Character, Integer> charToIndex;
    private final Character[] maxSeries;
    private final int maxNumber;
    private final Character[] currentSeries;
    private int currentNumber;
    private final Region region;
    private final Comparator<Character> customOrderComparator;

    public CarNumberIterator(List<Character> validChars, int minNumber, int maxNumber, Region region) {
        if (validChars.isEmpty() || maxNumber < minNumber) {
            throw new InvalidCarNumberPatternException();
        }
        this.validChars = validChars;
        charToIndex = new HashMap<>(validChars.size());
        for (int i = 0; i < validChars.size(); i++) {
            charToIndex.put(validChars.get(i), i);
        }
        customOrderComparator = (ch1, ch2) -> charToIndex.get(ch1).compareTo(charToIndex.get(ch2));
        this.maxNumber = maxNumber;
        this.region = region;
        currentNumber = minNumber;
        char startChar = validChars.get(0);
        currentSeries = new Character[] {startChar, startChar, startChar};
        char endChar = validChars.get(validChars.size() - 1);
        maxSeries = new Character[] {endChar, endChar, endChar};
    }

    @Override
    public boolean hasNext() {
        return currentNumber < maxNumber || Arrays.compare(currentSeries, maxSeries, customOrderComparator) < 0;
    }

    @Override
    public CarNumber next() {
        try {
            CarNumber next = CarNumber.createCarNumber(currentNumber, Joiner.on("").join(currentSeries), region);
            if (currentNumber < maxNumber) {
                currentNumber++;
            } else {
                currentNumber = 0;
                incrementSeries();
            }
            return next;
        } catch (InvalidCarNumberException e) {
            throw new RuntimeException(e);
        }
    }

    private void incrementSeries() {
        for (int i = currentSeries.length - 1; i >= 0; i--) {
            char endChar = validChars.get(validChars.size() - 1);
            if (currentSeries[i] != endChar) {
                char nextChar = validChars.get(charToIndex.get(currentSeries[i]) + 1);
                currentSeries[i] = nextChar;
                break;
            } else {
                char firstChar = validChars.get(0);
                currentSeries[i] = firstChar;
            }
        }
    }

}
