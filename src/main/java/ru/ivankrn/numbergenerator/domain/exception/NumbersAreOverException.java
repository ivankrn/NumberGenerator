package ru.ivankrn.numbergenerator.domain.exception;

public class NumbersAreOverException extends Exception {

    public NumbersAreOverException(String errorMessage) {
        super(errorMessage);
    }

}
