package ru.ivankrn.numbergenerator.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.ivankrn.numbergenerator.domain.exception.NumbersRanOutException;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    public static final String NUMBERS_ARE_OVER_MESSAGE = "Numbers ran out";

    @ExceptionHandler(NumbersRanOutException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NumbersRanOutException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ErrorResponse(status, NUMBERS_ARE_OVER_MESSAGE));
    }

}
