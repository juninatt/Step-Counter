package com.nexergroup.boostapp.java.step.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DateTimeValueException extends RuntimeException{
    public DateTimeValueException(String message) {
        super(message);
    }
}
