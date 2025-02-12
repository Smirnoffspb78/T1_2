package ru.t1.java.demo.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerApplication {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleResponseStatusException(EntityNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleBadRequest(RuntimeException ex) {
        return ex.getMessage();
    }
}
