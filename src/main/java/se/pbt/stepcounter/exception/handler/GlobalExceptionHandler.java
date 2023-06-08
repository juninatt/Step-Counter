package se.pbt.stepcounter.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import se.pbt.stepcounter.exception.NotFoundException;
import se.pbt.stepcounter.exception.InvalidUserIdException;

/**
 *
 CustomGlobalExceptionHandler is a class that extends ResponseEntityExceptionHandler and provides
 customized exception handling for the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse<?>> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
		var error = new ErrorResponse<>();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setMessage(illegalArgumentException.getMessage());
		error.setTimeStamp(System.currentTimeMillis());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse<?>> handleNotFoundException(NotFoundException notFoundException) {
		var errorResponse = new ErrorResponse<>();
		errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
		errorResponse.setMessage(notFoundException.getMessage());
		errorResponse.setTimeStamp(System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(InvalidUserIdException.class)
	public ResponseEntity<ErrorResponse<?>> handleUserIdValueException(InvalidUserIdException invalidUserIdException) {
		var errorResponse = new ErrorResponse<>();
		errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
		errorResponse.setMessage(invalidUserIdException.getMessage());
		errorResponse.setTimeStamp(System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
}
