package com.nexergroup.boostapp.java.step.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The DateTimeValueException wraps checked exceptions for Step Entity validation and
 * enriches them with a custom error message.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class DateTimeValueException extends RuntimeException{
    public DateTimeValueException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
