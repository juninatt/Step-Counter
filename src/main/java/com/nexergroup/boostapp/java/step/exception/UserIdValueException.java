package com.nexergroup.boostapp.java.step.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class UserIdValueException extends RuntimeException {
    public UserIdValueException(String message) {
        super(message);
    }
}
