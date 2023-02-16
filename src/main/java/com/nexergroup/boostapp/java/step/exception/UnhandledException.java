package com.nexergroup.boostapp.java.step.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Not best practise!!
 * The UnhandledException wraps unhandled exceptions for Step Entity validation
 * and with a custom error message.
 */

@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
public class UnhandledException extends RuntimeException{
    public UnhandledException(String message) {
        super(message);
    }
}
