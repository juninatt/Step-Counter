package se.pbt.stepcounter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The InvalidUserIdValueException is thrown when an invalid user ID value is encountered.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidUserIdException extends RuntimeException {

    public InvalidUserIdException() { super("User ID is invalid.");
    }

    public InvalidUserIdException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
