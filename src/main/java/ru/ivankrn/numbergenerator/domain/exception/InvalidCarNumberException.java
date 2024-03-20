package ru.ivankrn.numbergenerator.domain.exception;

public class InvalidCarNumberException extends Exception {

    public InvalidCarNumberException(String errorMessage) {
        super(errorMessage);
    }

}
