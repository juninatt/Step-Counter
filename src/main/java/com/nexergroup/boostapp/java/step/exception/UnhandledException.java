package com.nexergroup.boostapp.java.step.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Not best practise!!
 */

@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
public class UnhandledException extends RuntimeException{
    public UnhandledException(String message) {
        super(message);
    }
}
