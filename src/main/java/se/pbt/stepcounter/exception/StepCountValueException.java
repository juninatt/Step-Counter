package se.pbt.stepcounter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * The StepCountValueException wraps checked exceptions for Step Entity validation
 * and enriches them with a custom error message.
 */
@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class StepCountValueException extends RuntimeException{
    public StepCountValueException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return HttpStatus.NO_CONTENT;
    }
}
