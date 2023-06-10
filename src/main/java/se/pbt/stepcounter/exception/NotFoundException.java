package se.pbt.stepcounter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The NotFoundException is thrown when the required resource cannot be found.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{

    public NotFoundException() { super("Not found"); }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) { super(message, cause); }

    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
