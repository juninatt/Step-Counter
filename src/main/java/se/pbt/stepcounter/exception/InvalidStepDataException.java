package se.pbt.stepcounter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidStepDataException extends RuntimeException{

    public InvalidStepDataException() {
        super("Data is invalid.");
    }
    public InvalidStepDataException(String message) {
        super(message);
    }

    public InvalidStepDataException(String message, Throwable cause) { super(message, cause);}

    public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST;  }
}
