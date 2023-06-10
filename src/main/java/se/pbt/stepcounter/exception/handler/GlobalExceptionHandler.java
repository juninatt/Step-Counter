package se.pbt.stepcounter.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import se.pbt.stepcounter.exception.InvalidStepDataException;
import se.pbt.stepcounter.exception.InvalidUserIdException;
import se.pbt.stepcounter.exception.NotFoundException;

/**
 *
 CustomGlobalExceptionHandler is a class that extends ResponseEntityExceptionHandler and provides
 customized exception handling for the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse<?>> handleIllegalArgumentException(IllegalArgumentException exception) {
		return createErrorResponse(
				HttpStatus.BAD_REQUEST,
				exception.getMessage(),
				exception.getCause()
		);
	}

	@ExceptionHandler(InvalidStepDataException.class)
	public ResponseEntity<ErrorResponse<?>> handleValidationFailedException(InvalidUserIdException exception) {
		var errorResponse = new ErrorResponse<>();
		errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
		errorResponse.setMessage(exception.getMessage());
		errorResponse.setTimeStamp(System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse<?>> handleNotFoundException(NotFoundException exception) {
		return createErrorResponse(
				HttpStatus.NOT_FOUND,
				exception.getMessage(),
				exception
		);
	}

	@ExceptionHandler(InvalidUserIdException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse<?>> handleUserIdValueException(InvalidUserIdException exception) {
		return createErrorResponse(
				HttpStatus.BAD_REQUEST,
				exception.getMessage(),
				exception
		);
	}

	@ExceptionHandler(NullPointerException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse<?>> handleNullPointerException(NullPointerException exception) {
		return createErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR,
				exception.getMessage(),
				exception
		);
	}


	private ResponseEntity<ErrorResponse<?>> createErrorResponse(HttpStatus status, String message, Throwable cause) {
		var errorResponse = new ErrorResponse<>();
		errorResponse.setStatus(status.value());
		errorResponse.setMessage(message);
		errorResponse.setTimeStamp(System.currentTimeMillis());
		errorResponse.setDetails(cause);
		return ResponseEntity.status(status).body(errorResponse);
	}

}
