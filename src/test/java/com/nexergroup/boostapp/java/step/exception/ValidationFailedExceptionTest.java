package com.nexergroup.boostapp.java.step.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("ValidationFailedException Unit Tests")
public class ValidationFailedExceptionTest {

    @Test
    @DisplayName("Test ValidationFailedException Constructor")
    public void testValidationFailedExceptionConstructor() {
        String errorMessage = "Validation failed for step entity";
        ValidationFailedException exception = new ValidationFailedException(errorMessage);
        Assertions.assertEquals(errorMessage, exception.getMessage(), "Error message is not correct");
        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatus(), "HttpStatus is not CONFLICT");
    }
}
