package se.pbt.stepcounter.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 CustomGlobalExceptionHandler is a class that extends ResponseEntityExceptionHandler and provides
 customized exception handling for the application.
 */
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Handle the NotFoundException by returning an HTTP 404 Not Found status to the client.
	 *
	 * @param response HttpServletResponse - an object to represent the HTTP response sent to the client
	 * @throws IOException if an input or output error is detected when the servlet handles the request
	 */
	@ExceptionHandler(NotFoundException.class)
	public void customerHandleNotFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value());
	}

	/**
	 * Handle the MethodArgumentNotValidException by returning an HTTP 400 Bad Request status to the client
	 * along with a map of field errors.
	 *
	 * @param notValidException The exception MethodArgumentNotValidException thrown when argument annotated with @Valid failed validation
	 * @param httpHeaders HttpHeaders - a object represent HTTP headers
	 * @param httpStatus HttpStatus - a object represent HTTP status
	 * @param webRequest WebRequest - a object to provide request/response abstractions
	 * @return a ResponseEntity with the given status, httpHeaders, and body
	 */
	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException notValidException, HttpHeaders httpHeaders,
			HttpStatus httpStatus, WebRequest webRequest) {
		Map<String, String> errors = new HashMap<>();
		notValidException.getBindingResult().getAllErrors().forEach((error) -> {
			String name = (error instanceof org.springframework.validation.FieldError) ? ((FieldError) error).getField()
					: error.getObjectName();
			String errorMessage = error.getDefaultMessage();
			errors.put(name, errorMessage);
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}


}
